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
def sonarQubecheck(sonarQubecheck) {
    if (sonarQubecheck == "true") {
        // Retrieve SonarQube credentials from AWS SSM Parameter Store
         SONAR_USER = sh(script: 'aws ssm get-parameters --region us-east-1 --names sonarqube.user --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()
         SONAR_PASS = sh(script: 'aws ssm get-parameters --region us-east-1 --names sonarqube.pass --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()

        // Wrap the password to mask it in the Jenkins build log
        wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [[password: "${SONAR_PASS}", var: 'SECRET']]]) {
            // Execute SonarQube analysis
            sh "sonar-scanner -Dsonar.host.url=http://34.124.155.157:9000 -Dsonar.login='${SONAR_USER}' -Dsonar.password='${SONAR_PASS}' -Dsonar.projectKey=${component} -Dsonar.qualitygate.wait=true ${SONAR_EXTRA_OPTS}"
        }
    }
}

def email(email_note){
    mail bcc: '', body: "Job Failed - ${JOB_BASE_NAME}\nJenkins URL - ${JOB_URL}", cc: '', from: 'mpvarma9997@gmail.com', replyTo: '', subject: "Jenkins Job Failed - ${JOB_BASE_NAME}", to: 'phani.manthena27@gmail.com'

}