pipeline {
    agent { label 'linux64' }
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
                sh 'mvn -B package'
            }
        }
        stage('Polaris') {
            when {
                anyOf {
                    branch 'main'
                    branch pattern: "PR-\\d+", comparator: "REGEXP"
                }
            }
            steps {
                synopsys_scan product: 'polaris',
                    polaris_assessment_types: 'SCA',
                    polaris_prComment_enabled: true,
                    polaris_application_name: 'chuckaude-hello-java', 
                    polaris_project_name: 'hello-java'
            }
        }
    }
    post {
        always {
            //archiveArtifacts allowEmptyArchive: true, artifacts: '.bridge/bridge.log, .bridge/*/idir/build-log.txt, .bridge/*/report.sarif.json'
            //zip archive: true, dir: '.bridge', zipFile: 'bridge-logs.zip'
            cleanWs()
        }
    }
}
