def call() {
    pipeline {
        options {
            ansiColor('xterm')
        }
        agent {
            node {
                label 'workstation'
            }
        }
        parameters {
            choice(name: 'ACTION', choices: ['create', 'destroy'], description: 'Select an action: create or destroy')
            string(name: 'INFRA_ENV', defaultValue: '', description: 'Enter Environment like Dev or Prod')
        }
        stages {
            stage('Terraform Init') {
                steps {
                    script {
                        sh "terraform init -backend-config=env-${INFRA_ENV}/state.tfvars"
                    }
                }
            }
            stage('Terraform Apply or Destroy') {
                steps {
                    script {
                        if (params.ACTION == 'create') {
                            sh "terraform apply -auto-approve -var-file=env-${INFRA_ENV}/main.tfvars"
                        } else if (params.ACTION == 'destroy') {
                            sh "terraform destroy -auto-approve -var-file=env-${INFRA_ENV}/main.tfvars"
                        } else {
                            error "Invalid action selected. Please choose either 'create' or 'destroy'."
                        }
                    }
                }
            }
        }
        post {
            always {
                cleanWs()
            }
        }
    }
}
