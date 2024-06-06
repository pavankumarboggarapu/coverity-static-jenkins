// example Jenkinsfile for Black Duck scans using the Synopsys Security Scan Plugin
// https://plugins.jenkins.io/synopsys-security-scan
pipeline {
    agent { label 'linux64' }
    environment {
        REPO_NAME = "${env.GIT_URL.tokenize('/.')[-2]}"
    }
    tools {
        maven 'maven-3.9'
        jdk 'openjdk-17'
    }
    stages {
        stage('Environment') {
            steps {
                sh 'env | sort'
            }
        }
        stage('Build') {
            steps {
                sh 'mvn -B compile'
            }
        }
        stage('Test') {
            steps {
                sh 'mvn -B test'
            }
        }
        stage('Black Duck') {
            steps {
                script {
                    status = synopsys_scan product: 'blackduck',
                        blackduck_automation_prcomment: true,
                        blackduck_reports_sarif_create: true
                    if (status == 8) { unstable 'policy violation' }
                    else if (status != 0) { error 'plugin failure' }
                }
            }
        }
        stage('Deploy') {
            steps {
                sh 'mvn -B -DskipTests install'
            }
        }
    }
    post {
        always {
            archiveArtifacts allowEmptyArchive: true, artifacts: '*_BlackDuck_RiskReport.pdf'
            //zip archive: true, dir: '.bridge', zipFile: 'bridge-logs.zip'
            cleanWs()
        }
    }
}
