def call() {
    pipeline {
        agent any {
            stages {
                stage('Compile/Build') {
                    steps {
                        echo 'Compiling'
                    }
                    stage('Unit Test') {
                        steps {
                            echo 'Unit Test'
                        }
                    }

                }
            }
        }
    }
}