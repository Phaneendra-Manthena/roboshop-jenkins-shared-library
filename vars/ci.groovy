def call() {
      if (!env.SONAR_EXTRA_OPTS) {
        env.SONAR_EXTRA_OPTS = " "
       }
    try {
        pipeline {
            agent {
                label 'workstation'
            }
            stages {
//                stage('Clean WorkSpace') {
//                    steps {
//                        script {
//                            cleanWs()
//                        }
//                    }
//                }
                stage('Compile/Build') {
                    steps {
                        script {
                            common.compile()
                        }
                    }
                }
                stage('Unit Tests') {
                    steps {
                        script {
                            common.unittests()
                        }
                    }
                }
                stage('OWASP Dependency Check') {
                    steps {
                        script {
                            common.dependencyCheck()
                        }
                    }
                }
                stage('Quality Control') {
                    environment {
//                        SONAR_PASS = sh(script: "aws secretsmanager get-secret-value --secret-id sonar.pass | jq -r '.SecretString'", returnStdout: true).trim()
                        SONAR_USER = sh(script: 'aws ssm get-parameters --region us-east-1 --names sonarqube.user  --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()

                        SONAR_PASS = sh(script: 'aws ssm get-parameters --region us-east-1 --names sonarqube.pass  --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()

                    }
                    steps {
                        wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [[password: "${SONAR_PASS}", var: 'SECRET']]]) {
                            sh "sonar-scanner -Dsonar.host.url=http://35.240.252.59:9000 -Dsonar.login='${SONAR_USER}' -Dsonar.password='${SONAR_PASS}' -Dsonar.projectKey=${component} -Dsonar.qualitygate.wait=true ${SONAR_EXTRA_OPTS}"
                        }
                    }

                }
                stage('Cleaning WorkSpace') {
                    steps{
                        script{
                            cleanWs()
                        }
                    }
                }
            }
        }
    } catch(Exception e) {
        common.email("Failed")
    }
}
