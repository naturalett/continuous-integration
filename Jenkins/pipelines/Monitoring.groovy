@Grab(group = 'com.twilio.sdk', module = 'twilio', version = '9.2.1')
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import groovy.transform.Field

pipeline {
    agent {
        docker {
            image 'docker:19.03.12'
            args '-v /var/run/docker.sock:/var/run/docker.sock -v /var/workshop-creds:/home'
        }
    }
    stages {
        stage('Do job stage') {
            steps {
                git branch: 'main', url: 'https://github.com/naturalett/continuous-integration.git'
                sh 'ls -al'
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