def call () {
    pipeline {
//        options {
//            ansiColor('xterm')
//        }
        agent {
            node {
                label 'workstation'
            }
        }
        parameters {
            string(name: 'INFRA_ENV', defaultValue: '', description: 'Enter Environment like Dev or Prod')
        }
        stages {
            stage('Terraform Init') {
                steps{
                    sh "terraform init -backend-config=env-${INFRA_ENV}/state.tfvars"
                }
            }
        }
    }
}