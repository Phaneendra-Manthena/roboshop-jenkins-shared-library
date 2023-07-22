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
                        // ... (rest of the environment configuration)
                    }
                    steps {
                        wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [[password: "${SONAR_PASS}", var: 'SECRET']]]) {
                            // ... (rest of the steps)
                            sh "echo Sonar Scan"
                        }
                    }
                }
            }
            // Move the 'Upload to Centralized Place' stage outside of the 'stages' block
            stage('Upload to Centralized Place') {
                steps {
                    script {
                        if (env.PUSH_CODE == "true") {
                            echo 'Uploading to Centralized Place'
                        }
                    }
                }
            }
            stage('Cleaning WorkSpace') {
                steps {
                    script {
                        cleanWs()
                    }
                }
            }
        }
    } catch (Exception email_note) {
        common.email("Failed")
    }
}
