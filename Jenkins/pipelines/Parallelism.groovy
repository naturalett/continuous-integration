import groovy.transform.Field
@Field Map parallel_deploys = [: ]
@Field String customImage, applicationDir = "Application", dockerHubOwner = "naturalett"

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
                git branch: params.branch, url: 'https://github.com/naturalett/continuous-integration.git'
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
        stage('Parallel Test') {
            steps {
                script {
                    ["test_exit", "test_home"].each {
                        test_path ->
                            parallel_deploys[test_path] = {
                                stage("Running ${test_path}") {
                                    customImage.inside {
                                        sh """#!/bin/bash
                                        cd /app
                                        pytest ${test_path}.py -v --junitxml='${test_path}.xml'"""
                                        test_result = sh (
                                            script:  "cat /app/${test_path}.xml",
                                            returnStdout: true).trim()
                                    }
                                    writeFile file: "${test_path}.xml", text: test_result
                                }
                            }
                    }
                    parallel parallel_deploys
                }
            }
        }
        stage('Display Results') {
            steps {
                echo 'Displaying Results...'
                junit allowEmptyResults: true, testResults: 'test*.xml'
            }
        }
    }
    post {
        always {
            script {
                deleteDir()
            }
        }
    }
}