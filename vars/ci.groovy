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
                    steps {
                        script{
                            common.sonarQubecheck(true)
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
