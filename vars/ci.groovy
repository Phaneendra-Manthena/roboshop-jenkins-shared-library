def call() {
    pipeline {
        agent {
            label 'workstation'
        }
        stages {
            stage('Compile/Build') {
                steps {
                    script{
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
            stage('Quality Control') {
                steps {
                    echo 'Quality Control'
                }
            }
        }
    }
}