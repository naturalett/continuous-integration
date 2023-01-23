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
                deleteDir()
                git branch: 'main', url: 'https://github.com/naturalett/continuous-integration.git'
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
    }
    post {
        success {
            script {
                currentBuild.description = "Passed successfully!"
            }
        }
    }
}