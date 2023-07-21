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

def email(email_notes){
    mail bcc: '', body: 'Test', cc: '', from: 'mpvarma9997@gmail.com', replyTo: '', subject: 'Test from Jenkins', to: 'phani.manthena27@gmail.com'
}