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
                    when {
                        expression { sonarQubecheck == "true" }
                    }
                    environment {
                        SONAR_USER = sh(script: 'aws ssm get-parameters --region us-east-1 --names sonarqube.user  --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()
                        SONAR_PASS = sh(script: 'aws ssm get-parameters --region us-east-1 --names sonarqube.pass  --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()
                    }
                    steps {
                        wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [[password: "${SONAR_PASS}", var: 'SECRET']]]) {
                            sh "sonar-scanner -Dsonar.host.url=http://34.124.155.157:9000 -Dsonar.login='${SONAR_USER}' -Dsonar.password='${SONAR_PASS}' -Dsonar.projectKey=${component} -Dsonar.qualitygate.wait=true ${SONAR_EXTRA_OPTS}"
                        }
                    }
                }

                stage('Quality Control Skipped') {
                    when {
                        expression { sonarQubecheck != "true" }
                    }
                    steps {
                        echo "Scan Skipped"
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
