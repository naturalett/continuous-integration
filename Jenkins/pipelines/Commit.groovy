import groovy.transform.Field
@Field String applicationDir = "Application", dockerHubOwner = "naturalett"

pipeline {
    agent {
        docker {
            image 'docker:19.03.12'
            args '-v /var/run/docker.sock:/var/run/docker.sock'
        }
    }
    stages {
        stage('Clone') {
            steps {
                deleteDir()
                git branch: 'main', url: 'https://github.com/naturalett/continuous-integration.git'
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
                            sh """
                            #!/bin/bash
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
                    junit allowEmptyResults: true, testResults: 'test-results.xml'
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
}