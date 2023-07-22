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
            sh 'npm test || true'
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
    } else {
        // Execute this block of code when dependencyCheck is NOT equal to "true"
        echo 'Skipping Dependency checks'
    }
}
def sonarScan(String sonarUrl, String sonarUser, String sonarPass, String component, String sonarExtraOpts) {
    sh "sonar-scanner -Dsonar.host.url=${sonarUrl} -Dsonar.login=${sonarUser} -Dsonar.password=${sonarPass} -Dsonar.projectKey=${component} -Dsonar.qualitygate.wait=true ${sonarExtraOpts}"
}

def email(email_note){
    mail bcc: '', body: "Job Failed - ${JOB_BASE_NAME}\nJenkins URL - ${JOB_URL}", cc: '', from: 'mpvarma9997@gmail.com', replyTo: '', subject: "Jenkins Job Failed - ${JOB_BASE_NAME}", to: 'phani.manthena27@gmail.com'

}