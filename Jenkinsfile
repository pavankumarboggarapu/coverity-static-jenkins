// example Jenkinsfile for Polaris scans using the Synopsys Security Scan Plugin
// https://plugins.jenkins.io/synopsys-security-scan
pipeline {
    agent { label 'linux64' }
    environment {
        REPO_NAME = "${env.GIT_URL.tokenize('/.')[-2]}"
        FULLSCAN = "${env.BRANCH_NAME ==~ /^(main|master|develop|stage|release)$/ ? 'true' : 'false'}"
        PRSCAN = "${env.CHANGE_TARGET ==~ /^(main|master|develop|stage|release)$/ ? 'true' : 'false'}"
        BRIDGECLI_LINUX64 = 'https://sig-repo.synopsys.com/artifactory/bds-integrations-release/com/synopsys/integration/synopsys-bridge/latest/synopsys-bridge-linux64.zip'
        BRIDGE_POLARIS_SERVERURL = 'https://poc.polaris.synopsys.com'
        BRIDGE_POLARIS_ACCESSTOKEN = credentials('poc.polaris.synopsys.com')
        BRIDGE_POLARIS_APPLICATION_NAME = "chuckaude-${env.REPO_NAME}"
        BRIDGE_POLARIS_PROJECT_NAME = "${env.REPO_NAME}"
        BRIDGE_POLARIS_BRANCH_NAME = "$BRANCH_NAME"
        BRIDGE_POLARIS_ASSESSMENT_TYPES = 'SAST,SCA'
        GITHUB_TOKEN = credentials('github-pat')
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
                sh 'mvn -B package'
            }
        }
/*
        stage('Polaris') {
            steps {
                synopsys_scan product: 'polaris',
                    polaris_assessment_types: 'SAST,SCA',
                    polaris_application_name: "chuckaude-$REPO_NAME",
                    polaris_project_name: "$REPO_NAME",
                    polaris_branch_name: "$BRANCH_NAME",
                    polaris_prComment_enabled: true,
                    polaris_reports_sarif_create: true,
                    mark_build_status: 'UNSTABLE',
                    github_token: "$GITHUB_TOKEN"
            }
        }
*/
        stage('Polaris Full Scan') {
            when { environment name: 'FULLSCAN', value: 'true' }
            steps {
                script {
                    status = sh returnStatus: true, script: '''
                        curl -fLsS -o bridge.zip $BRIDGECLI_LINUX64 && unzip -qo -d $WORKSPACE_TMP bridge.zip && rm -f bridge.zip
                        $WORKSPACE_TMP/synopsys-bridge --verbose --stage polaris \
                            polaris.reports.sarif.create=true
                    '''
                    if (status == 8) { unstable 'policy violation' }
                    else if (status != 0) { error 'scan failure' }
                }
            }
        }
        stage('Polaris PR Scan') {
            when { environment name: 'PRSCAN', value: 'true' }
            steps {
                script {
                    status = sh returnStatus: true, script: '''
                        curl -fLsS -o bridge.zip $BRIDGECLI_LINUX64 && unzip -qo -d $WORKSPACE_TMP bridge.zip && rm -f bridge.zip
                        $WORKSPACE_TMP/synopsys-bridge --verbose --stage polaris \
                            polaris.prcomment.enabled=true \
                            polaris.branch.parent.name=$CHANGE_TARGET \
                            github.repository.name=$REPO_NAME \
                            github.repository.branch.name=$BRANCH_NAME \
                            github.repository.owner.name=chuckaude-org \
                            github.repository.pull.number=$CHANGE_ID \
                            github.user.token=$GITHUB_TOKEN
                    '''
                    if (status == 8) { unstable 'policy violation' }
                    else if (status != 0) { error 'scan failure' }
                }
            }
        }
    }
    post {
        always {
            archiveArtifacts allowEmptyArchive: true, artifacts: '.bridge/bridge.log, .bridge/*/idir/build-log.txt, .bridge/*/report.sarif.json'
            //zip archive: true, dir: '.bridge', zipFile: 'bridge-logs.zip'
            cleanWs()
        }
    }
}
