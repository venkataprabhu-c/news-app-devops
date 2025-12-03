pipeline {
    agent { label 'java' }

    environment {
        TOMCAT_USER = "tomcat"
        TOMCAT_HOST = "13.48.133.49" //your-server-ip
        TOMCAT_PATH = "/opt/tomcat10/webapps"
        SSH_CRED = "1611344c-d6a7-4f9f-9a3e-e15aba774122"    // ssh-tomcat-key Jenkins SSH credentials ID
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'feature-1', url: 'https://github.com/venkataprabhu-c/news-app-devops.git'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests=false'
            }
        }

        stage('Run Tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Publish Test Reports') {
            steps {
                junit '**/target/surefire-reports/*.xml'
            }
        }

        stage('Deploy WAR to Tomcat') {
            steps {
                sshagent([env.SSH_CRED]) {

                    sh """
                        echo "Removing old deployment..."
                        ssh ${TOMCAT_USER}@${TOMCAT_HOST} 'rm -rf ${TOMCAT_PATH}/news-app ${TOMCAT_PATH}/news-app.war'

                        echo "Copying new WAR..."
                        scp target/news-app.war ${TOMCAT_USER}@${TOMCAT_HOST}:${TOMCAT_PATH}/

                        echo "Restarting Tomcat..."
                        ssh ${TOMCAT_USER}@${TOMCAT_HOST} 'sudo systemctl restart tomcat'
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Build and deployment completed successfully!'
        }
        failure {
            echo 'Build or deployment failed. Check logs for details.'
        }
    }
}
