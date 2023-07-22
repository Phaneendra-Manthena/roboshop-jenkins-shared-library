def call() {
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
                    environment {
                        SONAR_USER = sh ( script: 'aws ssm get-parameters --region us-east-1 --names sonarqube.user  --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()
                        SONAR_PASS = sh ( script: 'aws ssm get-parameters --region us-east-1 --names sonarqube.pass  --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()
                    }
                    steps {
                        wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [[password: "${SONAR_PASS}", var: 'SECRET']]]) {
                            sh "sonar-scanner -Dsonar.host.url=http://35.240.252.59:9000 -Dsonar.login='${SONAR_USER}' -Dsonar.password='${SONAR_PASS}' -Dsonar.projectKey=cart"
                        }
                    }
                }
            }
        }
    } catch (Exception e) {
        common.email("Failed")
    }
}
