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
    }
    post {
        success {
            script {
                currentBuild.description = "Passed successfully!"
            }
        }
    }
}