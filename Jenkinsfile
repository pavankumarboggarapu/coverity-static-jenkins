// example Jenkinsfile for Polaris scans using the Synopsys Security Scan Plugin
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
        stage('polaris') {
            steps {
                synopsys_scan product: 'polaris',
                    polaris_assessment_types: 'SAST,SCA',
                    polaris_application_name: "chuckaude-$REPO_NAME",
                    polaris_project_name: "$REPO_NAME",
                    polaris_branch_name: "$BRANCH_NAME",
					polaris_prComment_enabled: true,
                    polaris_reports_sarif_create: true,
                    mark_build_status: 'UNSTABLE',
            }
        }
        stage('Deploy') {
            steps {
                sh 'mvn -B install'
            }
        }
    }
    post {
        always {
            archiveArtifacts allowEmptyArchive: true, artifacts: '.bridge/bridge.log, .bridge/*/idir/build-log.txt'
            //zip archive: true, dir: '.bridge', zipFile: 'bridge-logs.zip'
            cleanWs()
        }
    }
}
