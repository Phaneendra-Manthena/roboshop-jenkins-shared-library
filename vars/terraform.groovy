def call () {
    pipeline {
        agent {
            node {
                label 'slave'
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