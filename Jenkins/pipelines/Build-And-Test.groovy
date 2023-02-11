import groovy.transform.Field
@Field String customImage, applicationDir = "Application"

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
                git branch: 'main', url: 'https://github.com/naturalett/continuous-integration.git'
            }
        }
        stage('Initialization') {
            steps {
                script {
                    load "/var/workshop-creds/env-file.groovy"
                }
            }
        }
        stage('Build') {
            steps {
                script {
                    dir(applicationDir) {
                        // https://github.com/naturalett/continuous-integration/tree/main/Application
                        customImage = docker.build("${env.dockerHubOwner}/hello-world:${env.BUILD_ID}")
                    }
                }
            }
        }
        stage('Test') {
            steps {
                script {
                    customImage.inside {
                        sh """#!/bin/bash
                        cd /app
                        pytest test_*.py -v --junitxml='test-results.xml'"""
                    }
                    deleteDir()
                }
            }
        }
    }
}