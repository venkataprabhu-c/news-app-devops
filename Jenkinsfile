pipeline {
    agent none

    stages {

        stage('Checkout') {
            agent { label 'java' }
            steps {
                sh "rm -rf news-app-devops"
                sh "git clone https://github.com/venkataprabhu-c/news-app-devops.git"
            }
        }

        stage('Build') {
            agent { label 'java' }
            steps {
                dir('news-app-devops') {
                    sh "mvn clean package"
                }
            }
        }

        stage('Test') {
            agent { label 'java' }
            steps {
                dir('news-app-devops') {
                    sh "mvn test"
                }
            }
        }

        stage('Versioning') {
            agent { label 'java' }
            steps {
                echo "version"
            }
        }

        stage('Deploy') {
            agent { label 'java' }
            steps {
                sh """
                sudo cp ${env.WORKSPACE}/news-app-devops/target/news-app.war \
                /opt/tomcat10/webapps/
                """
            }
        }

    }
}
