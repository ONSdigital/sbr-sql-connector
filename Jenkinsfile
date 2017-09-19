#!groovy
@Library('jenkins-pipeline-shared@develop') _

pipeline {
    agent any
    options {
        skipDefaultCheckout()
        buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '30'))
        timeout(time: 30, unit: 'MINUTES')
    }
    stages {
        stage('Checkout'){
            agent any
            steps{
                deleteDir()
                checkout scm
                stash name: 'app'
                sh "$SBT version"
                script {
                    version = '1.0.' + env.BUILD_NUMBER
                    currentBuild.displayName = version
                    // currentBuild.result = "SUCCESS"
                    env.NODE_STAGE = "Checkout"
                }
            }
        }
        stage('Build') {
            steps {
                colourText("info", "Building ${env.BUILD_ID} on ${env.JENKINS_URL} from branch ${env.BRANCH_NAME}")

                sh '''
                   $SBT clean compile
                   '''
            }
        }
        stage('Unit Test') {
            steps {
                colourText("info", "Unit test for ${env.BUILD_ID} on ${env.JENKINS_URL} on branch ${env.BRANCH_NAME}")

                sh '''
                   $SBT test
                   '''
            }
        }
        stage('Integration Test') {
            steps {
               colourText("info", "Integration test for ${env.BUILD_ID} on ${env.JENKINS_URL} on branch ${env.BRANCH_NAME}")
               echo '# No integration tests implemented here.'
            }
        }
        stage('Assembly') {
            steps {
                sh '''
                   $SBT assembly
                '''
            }
        }

    }
    post {
           always {
               script {
                   colourText("info", 'Post steps initiated')
                   deleteDir()
               }
           }
           success {
               colourText("success", "All stages complete. Build was successful.")
               // sendNotifications currentBuild.result, "\$SBR_EMAIL_LIST"
           }
           unstable {
               colourText("warn", "Something went wrong, build finished with result ${currentResult}. This may be caused by failed tests, code violation or in some cases unexpected interrupt.")
               // sendNotifications currentResult, "\$SBR_EMAIL_LIST", "${env.NODE_STAGE}"
           }
           failure {
               colourText("warn","Process failed at: ${env.NODE_STAGE}")
               // sendNotifications currentResult, "\$SBR_EMAIL_LIST", "${env.NODE_STAGE}"
           }
    }
}
