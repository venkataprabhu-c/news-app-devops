pipeline {
    agent any

    stages {

        stage('Checkout') {
            agent {label 'java' }
            steps {
                
                checkout scm
                script {
                    BRANCH_NAME = env.BRANCH_NAME ?: sh(script: "git rev-parse --abbrev-ref HEAD", returnStdout: true).trim()
                    COMMIT_SHORT = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    echo "Branch: ${BRANCH_NAME}, Commit: ${COMMIT_SHORT}"
                }
            }
        }

        stage('Test') {
            steps {
                sh "mvn test"
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Build') {
            steps {
                sh "mvn clean package"
            }
        }

        stage('Create Versioned Artifact') {
            steps {
                script {
                    baseVersion = sh(script: "mvn help:evaluate -Dexpression=project.version -q -DforceStdout", returnStdout:true).trim()
                    sanitizedBranch = BRANCH_NAME.replaceAll('[^A-Za-z0-9_.-]', '-')
                    BUILDNUM = env.BUILD_NUMBER
                    VERSION = "${baseVersion}-${sanitizedBranch}-build${BUILDNUM}-${COMMIT_SHORT}"
                    echo "Computed VERSION = ${VERSION}"

                    // Rename WAR for downstream stages
                    sh "cp target/*.war target/app-${VERSION}.war"
                }
            }
        }

        stage('Publish to Artifactory') {
            steps {
                script {
                    def artServer = Artifactory.server('ART_SERVER')

                    def uploadSpec = """{
                      "files": [
                        {
                          "pattern": "target/app-${VERSION}.war",
                          "target": "libs-release-local/myapp/${VERSION}/"
                        }
                      ]
                    }"""

                    echo "Uploading artifact to Artifactory..."
                    artServer.upload spec: uploadSpec

                    def buildInfo = Artifactory.newBuildInfo()
                    buildInfo.env.capture = true
                    artServer.publishBuildInfo(buildInfo)
                }
            }
        }

        stage('Deploy to Tomcat') {
            steps {
                script {
                    sshagent(['ssh-tomcat-key']) {

                        echo "Stopping Tomcat..."
                        sh "ssh tomcat@server 'sudo systemctl stop tomcat'"

                        echo 'Cleaning previous deployment...'
                        sh "ssh tomcat@server 'rm -f /opt/tomcat/webapps/myapp.war'"
                        sh "ssh tomcat@server 'rm -rf /opt/tomcat/webapps/myapp'"

                        echo "Copying new WAR..."
                        sh "scp target/app-${VERSION}.war tomcat@server:/opt/tomcat/webapps/"

                        echo "Starting Tomcat..."
                        sh "ssh tomcat@server 'sudo systemctl start tomcat'"
                    }
                }
            }
        }
    }
}
