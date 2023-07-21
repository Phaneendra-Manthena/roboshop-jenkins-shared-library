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
        try {
            sh 'npm test || true'
        } catch (Exception e) {
            common.email("Unit tests failed")
        }

    }
    if (app_lang == "java") {
        sh 'mvn test'
    }
    if (app_lang == "python") {
        sh 'python3 -m unittest'
    }
}

def email(email_notes){
    sh 'echo ${email_notes}'
}