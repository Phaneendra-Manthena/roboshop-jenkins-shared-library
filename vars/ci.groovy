def call() {
    pipeline {
        agent any
        stages {
            stage('Compile/Build') {
                steps {
                    echo 'Compiling'
                }
            }
            stage('Unit Tests') {
                steps {
                    echo 'Unit Tests'
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