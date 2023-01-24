@Grab(group = 'com.twilio.sdk', module = 'twilio', version = '9.2.1')
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import groovy.transform.Field
@Field String customImage, publicIP, applicationDir = "Application", dockerHubOwner = "naturalett"
@Field Map parallel_deploys = [: ]

pipeline {
    agent {
        docker {
            image 'docker:19.03.12'
            args '-v /var/run/docker.sock:/var/run/docker.sock -v /var/workshop-creds:/home'
        }
    }
    parameters {
        string defaultValue: 'main', description: 'Feature Branch', name: 'branch'
    }
    stages {
        stage('Clone') {
            steps {
                deleteDir()
                git branch: params.branch, url: 'https://github.com/naturalett/continuous-integration.git'
            }
        }
        stage('Initialization') {
            steps {
                script {
                    docker.image('alpine').inside {
                        sh """
                        apk add curl
                        curl http://checkip.amazonaws.com > publicIP""".trim()
                    }
                    publicIP = readFile('publicIP').trim()
                }
            }
        }
        stage('Build') {
            steps {
                script {
                    dir(applicationDir) {
                        customImage = docker.build("${dockerHubOwner}/hello-world:${env.BUILD_ID}")
                    }
                }
            }
        }
        stage('Test') {
            steps {
                script {
                    dir(applicationDir) {
                        docker.image('python:3.7-slim').inside {
                            sh """#!/bin/bash
                            python3 -m venv venv
                            source venv/bin/activate
                            pip install -r requirements.txt
                            pytest test_*.py -v --junitxml='test-results.xml'"""
                        }
                    }
                }
            }
        }
        stage('Display Results') {
            steps {
                dir(applicationDir) {
                    echo 'Displaying Results...'
                    junit allowEmptyResults: true, testResults: 'test*.xml'
                }
            }
        }
        stage('Artifact') {
            steps {
                script {
                    sh '/bin/sh /var/workshop-creds/docker_login.sh'
                    customImage.push()
                    customImage.push('latest')
                }
            }
        }
        stage('Deploy') {
            steps {
                sh "docker run -it --name python-hello-world-${env.BUILD_ID} -d -p81:81 ${dockerHubOwner}/hello-world:${env.BUILD_ID}"
                echo "Check you deployment in the following link: http://${publicIP}:81/home"
                sleep 60
            }
        }
        stage('CleanUp') {
            steps {
                sh "docker stop python-hello-world-${env.BUILD_ID} && docker rm python-hello-world-${env.BUILD_ID}"
            }
        }
    }
    post {
        success {
            script {
                try {
                    load "/var/workshop-creds/env-file.groovy"
                    Twilio.init(env.accountSid, env.authToken)
                    Message message = Message.creator(
                            new com.twilio.type.PhoneNumber(env.phoneNumber),
                            env.SERVICE_SID,
                            "Your Pipeline: ${env.JOB_NAME}, Number: ${env.BUILD_NUMBER} passed successfully")
                        .create();
                    echo message.getSid()
                } catch (Exception e) {
                    echo "Failed to load env-file.groovy"
                }
                currentBuild.description = "Passed successfully. Message sent!"
            }
        }
        failure {
            script {
                try {
                    load "/var/workshop-creds/env-file.groovy"
                    Twilio.init(env.accountSid, env.authToken)
                    Message message = Message.creator(
                            new com.twilio.type.PhoneNumber(env.phoneNumber),
                            env.SERVICE_SID,
                            "Your Pipeline: ${env.JOB_NAME}, Number: ${env.BUILD_NUMBER} failed")
                        .create();
                    echo message.getSid()
                } catch (Exception e) {
                    echo "Failed to load env-file.groovy"
                }
                currentBuild.description = "Failed. Message sent!"
            }
        }
        always {
            script {
                deleteDir()
            }
        }
    }
}