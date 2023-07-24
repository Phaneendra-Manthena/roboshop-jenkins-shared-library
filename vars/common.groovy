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
def artifactPush() {
    if (app_lang == "nodejs") {
        sh "echo ${TAG_NAME} > VERSION"
        sh "zip -r ${component}-${TAG_NAME}.zip node_modules server.js VERSION ${extraFiles}"
    }
    if (app_lang == "nginx") {
        sh "echo ${TAG_NAME} > VERSION"
        sh "zip -r ${component}-${TAG_NAME}.zip * -x Jenkinsfile"
    }

    NEXUS_USER = sh(script: 'aws ssm get-parameters --region us-east-1 --names nexus.user  --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()

    NEXUS_PASS = sh(script: 'aws ssm get-parameters --region us-east-1 --names nexus.pass  --with-decryption --query Parameters[0].Value | sed \'s/"//g\'', returnStdout: true).trim()

    // Use wrap step to mask the NEXUS_PASS variable
    wrap([$class: 'MaskPasswordsBuildWrapper', varPasswordPairs: [[password: "${NEXUS_PASS}", var: 'SECRET']]]) {
        // Your sensitive code block that needs to be wrapped with password masking
        sh "curl -v -u ${NEXUS_USER}:${NEXUS_PASS} --upload-file ${component}-${TAG_NAME}.zip http://35.221.213.122:8081/repository/${component}/${component}-${TAG_NAME}.zip"
    }
}


def email(email_note){
    mail bcc: '', body: "Job Failed - ${JOB_BASE_NAME}\nJenkins URL - ${JOB_URL}", cc: '', from: 'mpvarma9997@gmail.com', replyTo: '', subject: "Jenkins Job Failed - ${JOB_BASE_NAME}", to: 'phani.manthena27@gmail.com'

}