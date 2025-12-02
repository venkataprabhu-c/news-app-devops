pipeline {
    agent none

   // parameters {
    //    string(name: 'mcmd', defaultValue: 'clean', description: 'Maven clean parameter')
     //   booleanParam(name: 'SAMPLE_BOOLEAN', defaultValue: true, description: 'A boolean parameter')
       // choice(name: 'mcmd1', choices: ['validate', 'compile', 'package','test'], description: 'Choose one option')
   // } 

    stages {
        stage('Project feature1') {
            parallel {
                stage('Checkout') {
                    agent { label 'java' }
                    steps {
                   //     withCredentials([
                        //    usernamePassword(credentialsId: '15b0fdb3-ca60-425f-87a8-e3a90a71f883',
                           //                  usernameVariable: 'MY_USERNAME',
                             //                passwordVariable: 'MY_PASSWORD'),
                        //    sshUserPrivateKey(credentialsId: '8b1c5f3b-7314-47ec-8cec-ebf9b5f514d0',
                              //                keyFileVariable: 'KEY_FILE',
                                       //       usernameVariable: 'SSH_USER')
                //        ]) 
                      //  {
                            sh "rm -rf news-app-devops"
                            sh "git clone https://github.com/venkataprabhu-c/news-app-devops.git"
                        }
                    }
                }

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
                        sh "echo version"
                    }
                }

                stage('Deploy') {
                    agent { label 'java' }
                    steps {
                        sh "sudo cp /home/slave1/workspace/Project1_feature-1/target/news-app.war /opt/tomcat10/webapps/"
                    }
                }

            } // end parallel
        } // end stage
    } // end stages
}
