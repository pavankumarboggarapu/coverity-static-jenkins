pipeline {
    agent any

    environment {
        GITHUB_TOKEN = credentials('github-pkumarcoverity')
    }

    tools {
        maven 'maven-3'
        jdk 'openjdk-21'
    }

    stages {
        stage('Init') {
            steps {
                script {
                    // Get repo name from Git URL
                    def gitUrl = sh(script: "git config --get remote.origin.url", returnStdout: true).trim()
                    env.REPO_NAME = gitUrl.tokenize('/.git')[-1]

                    // Get current branch name
                    env.BRANCH_NAME = sh(script: "git rev-parse --abbrev-ref HEAD", returnStdout: true).trim()

                    echo "Repo Name: ${env.REPO_NAME}"
                    echo "Branch Name: ${env.BRANCH_NAME}"
                }
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B package'
            }
        }

        stage('Coverity Scan') {
            steps {
                security_scan product: 'coverity',
                    coverity_project_name: "${env.REPO_NAME}",
                    coverity_stream_name: "${env.REPO_NAME}-${env.BRANCH_NAME}",
                    coverity_args: "-o commit.connect.description=${env.BUILD_TAG}",
                    coverity_policy_view: 'Outstanding Issues',
                    coverity_local: true
                    coverity_prComment_enabled: true,
                    mark_build_status: 'UNSTABLE',
                    github_token: "${env.GITHUB_TOKEN}",
                    include_diagnostics: false
            }
        }
    }

    post {
        always {
            archiveArtifacts allowEmptyArchive: true, artifacts: '.bridge/bridge.log, .bridge/*/idir/build-log.txt'
            cleanWs()
        }
    }
}
