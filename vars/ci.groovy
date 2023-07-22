def call() {
    if (!env.SONAR_EXTRA_OPTS) {
        env.SONAR_EXTRA_OPTS = " "
    }
    if (!env.TAG_NAME) {
        env.PUSH_CODE = "false"
    } else {
        env.PUSH_CODE = "true"
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
                    environment {
//                        SONAR_PASS = sh(script: "aws secretsmanager get-secret-value --secret-id sonar.pass | jq -r '.SecretString'", returnStdout: true).trim()
                        SONAR_USER = sh(script: 'aws ssm get-parameters --region us-east-1 --names sonarqube.user  --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()

                        SONAR_PASS = sh(script: 'aws ssm get-parameters --region us-east-1 --names sonarqube.pass  --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()

                    }
                    steps {
                        wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [[password: "${SONAR_PASS}", var: 'SECRET']]]) {
//                            sh "sonar-scanner -Dsonar.host.url=http://34.124.155.157:9000 -Dsonar.login='${SONAR_USER}' -Dsonar.password='${SONAR_PASS}' -Dsonar.projectKey=${component} -Dsonar.qualitygate.wait=true ${SONAR_EXTRA_OPTS}"
                            sh "echo Sonar Scan"
                        }
                    }
                }
                // Move the 'Upload to Centralized Place' stage outside of the 'stages' block
                if (env.PUSH_CODE == "true") {
                    stage('Upload to Centralized Place') {
                        steps {
                            echo 'Uploading to Centralized Place'
                        }
                    }
                }
                // Add the 'Cleaning WorkSpace' stage outside of the 'stages' block
                stage('Cleaning WorkSpace') {
                    steps {
                        script {
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