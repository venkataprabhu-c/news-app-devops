pipeline {
    agent none

    stages {

        stage('Build') {
            agent { label 'java' }
            steps {
                sh "mvn clean package"
            }
        }

        stage('Test') {
            agent { label 'java' }
            steps {
                sh "mvn test"
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
                "/usr/bin/sudo cp /home/slave1/workspace/Project1_feature-1/target/news-app.war /opt/tomcat10/webapps/"
                """
            }
        }
    }
}
