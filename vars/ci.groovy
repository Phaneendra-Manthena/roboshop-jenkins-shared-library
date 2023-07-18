def call() {
    // Define your shared library logic here, if needed
    // This method will be called when the shared library is used in a Jenkinsfile
}

pipeline {
    agent any
    stages {
        stage('Compile/Build') {
            steps {
                echo 'Compiling'
            }
        }
        stage('Unit Test') {
            steps {
                echo 'Unit Test'
            }
        }
    }
}
