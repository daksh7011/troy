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
                    ./gradlew assemble 
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
    post {
        always {
            // run it always, regardless of pipeline status.
        }
        success{
            // run when build is success
        }
        failure {
            // run when build is failed.
        }
    }   
}
