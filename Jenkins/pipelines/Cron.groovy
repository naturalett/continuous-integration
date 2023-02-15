pipeline {
    agent {
        docker {
            image 'python'
        }
    }
    parameters {
        string defaultValue: 'main', description: 'Feature Branch', name: 'branch'
    }
    triggers {
        cron '* * * * *'
    }
    stages {
        stage('Do job stage') {
            steps {
                git branch: params.branch, url: 'https://github.com/naturalett/continuous-integration.git'
            }
        }
    }
}