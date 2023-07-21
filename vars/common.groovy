def compile() {
    if (app_lang == "nodejs") {
        sh 'npm install'
    }
    if (app_lang == "maven") {
        sh 'mvn compile'
    }
}
def unittests() {
    if (app_lang == "nodejs") {
            sh 'npm test'
        }

    if (app_lang == "java") {
        sh 'mvn test'
    }
    if (app_lang == "python") {
        sh 'python3 -m unittest'
    }
}
def dependencyCheck() {
    if (dependencyCheck == "true") {
        dependencyCheck additionalArguments: '--format HTML', odcInstallation: 'DP-Check'
    }
}

def sendJobFailedNotification() {
    // Define the email subject and body
    def subject = "Jenkins Job Failed - ${JOB_BASE_NAME}"
    def body = """Dear Team,

Unfortunately, the Jenkins job "${JOB_BASE_NAME}" has failed.

**Job Information:**
- Job Name: ${JOB_BASE_NAME}
- Jenkins URL: ${JOB_URL}

Please take the necessary actions to investigate and resolve the issue.

Best regards,
Your Name"""

    // Send the email notification
    emailext (
            to: 'phani.manthena27@gmail.com',
            subject: subject,
            body: body
    )
}
