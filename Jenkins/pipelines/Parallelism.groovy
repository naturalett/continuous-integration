import groovy.transform.Field
@Field Map parallel_deploys = [: ]
@Field String applicationDir = "Application"

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
        stage('Parallel Test') {
            steps {
                script {
                    ["test_exit", "test_home"].each {
                        test_path ->
                            parallel_deploys[test_path] = {
                                stage("Running ${test_path}") {
                                    dir(applicationDir) {
                                        docker.image('python:3.7-slim').inside {
                                            sh """
                                            #!/bin/bash
                                            python3 -m venv venv
                                            source venv/bin/activate
                                            pip install -r requirements.txt
                                            pytest ${test_path}.py -v --junitxml='${test_path}.xml'"""
                                        }
                                    }
                                }
                            }
                    }
                    parallel parallel_deploys
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
    }
}