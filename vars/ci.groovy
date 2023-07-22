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
//
                        SONAR_PASS = sh(script: 'aws ssm get-parameters --region us-east-1 --names sonarqube.pass  --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()
                    }
                    steps {
                        wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [[password: "${SONAR_PASS}", var: 'SECRET']]]) {
                            sonarScan(sonarUrl: 'http://34.124.155.157:9000', sonarUser: '${SONAR_USER}', sonarPass: '${SONAR_USER}', component: '${component}', sonarExtraOpts: '${SONAR_EXTRA_OPTS}')
//                            sh "echo Sonar Scan"
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
    } catch(Exception email_note) {
        common.email("Failed")
    }
}
