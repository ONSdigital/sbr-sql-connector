#!groovy
@Library('jenkins-pipeline-shared@feature/version') _

pipeline {
    environment {
        RELEASE_TYPE = "PATCH"
        
        GIT_TYPE = "Github"
        GIT_CREDS = "github-sbr-user"
    }
    options {
        skipDefaultCheckout()
        buildDiscarder(logRotator(numToKeepStr: '30', artifactNumToKeepStr: '30'))
        timeout(time: 15, unit: 'MINUTES')
        timestamps()
    }
    agent any
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
                    env.NODE_STAGE = "Checkout"
                }
            }
        }

        stage('Build'){
            agent any
            steps {
                colourText("info", "Building ${env.BUILD_ID} on ${env.JENKINS_URL} from branch ${env.BRANCH_NAME}")
                script {
                    env.NODE_STAGE = "Build"
                }
                sh '''
                $SBT clean compile test
                '''
            }
        }

        stage('Static Analysis') {
            agent any
            steps {
                parallel (
                        "Unit" :  {
                            colourText("info","Running unit tests")
                            // sh "$SBT test"
                        },
                        "Style" : {
                            colourText("info","Running style tests")
                            // sh '''
                            // $SBT scalastyleGenerateConfig
                            // $SBT scalastyle
                            // '''
                        },
                        "Additional" : {
                            colourText("info","Running additional tests")
                            // sh "$SBT scapegoat"
                        }
                )
            }
            post {
                always {
                    script {
                        env.NODE_STAGE = "Static Analysis"
                    }
                }
                success {
                    colourText("info","Generating reports for tests")
                    //   junit '**/target/test-reports/*.xml'

                    // step([$class: 'CoberturaPublisher', coberturaReportFile: '**/target/scala-2.11/coverage-report/*.xml'])
                    // step([$class: 'CheckStylePublisher', pattern: 'target/scalastyle-result.xml, target/scala-2.11/scapegoat-report/scapegoat-scalastyle.xml'])
                }
                failure {
                    colourText("warn","Failed to retrieve reports.")
                }
            }
        }


        stage("Releases"){
            agent any
            steps {
                script {
                    env.NODE_STAGE = "Releases"
                    currentTag = getLatestGitTag()
                    colourText("info", "Found latest tag: ${currentTag}")
                    newTag =  IncrementTag( currentTag, RELEASE_TYPE )
                    colourText("info", "Generated new tag: ${newTag}")
//                    push(newTag, currentTag)

                }
            }
        }

         stage ('Package') {
             agent any
             when {
                 anyOf {
                     branch "develop"
                     branch "release"
                     branch "master"
                 }
             }
             steps {
                 colourText("success", 'Packaging in progress...')
                 script {
                     env.NODE_STAGE = "Package"
                     sh '$SBT assembly'
                 }
             }
         }


         stage('Integration Tests') {
             agent any
             when {
                 anyOf {
                     branch "develop"
                     branch "release"
                 }
             }
             steps {
                 colourText("success", 'Integration Tests - For Release or Dev environment.')
                 script {
                     env.NODE_STAGE = "Integration Tests"
                     sh '$SBT it:test'
                 }
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
            sendNotifications currentBuild.result, "\$SBR_EMAIL_LIST"
        }
        unstable {
            colourText("warn", "Something went wrong, build finished with result ${currentResult}. This may be caused by failed tests, code violation or in some cases unexpected interrupt.")
            sendNotifications currentBuild.result, "\$SBR_EMAIL_LIST", "${env.NODE_STAGE}"
        }
        failure {
            colourText("warn","Process failed at: ${env.NODE_STAGE}")
            sendNotifications currentBuild.result, "\$SBR_EMAIL_LIST", "${env.NODE_STAGE}"
        }
    }
}



def push (String newTag, String currentTag) {
    echo "Pushing tag ${newTag} to ${GIT_TYPE}"
    GitRelease( GIT_CREDS, newTag, currentTag, "${env.BUILD_ID}", "${env.BRANCH_NAME}", GIT_TYPE)
}
