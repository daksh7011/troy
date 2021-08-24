pipeline {

    agent any
    tools {
        gradle 'gradle-7-2'
    }
    stages {
        
        stage("build") {
            steps {
                script {
                    echo "Building the app..."
                    sh "./gradlew assemble "
                }
            }
        }

        stage("test") {
            when {
                expression {
                    env.BRANCH_NAME != 'master'
                }
            }
            steps {
                script {
                    echo "Testing the app..."
                }
            }
        }

        stage("deploy") {
            steps {
                script {
                    echo "Deploying the app..."
                }
            }
        }

    } 
}
