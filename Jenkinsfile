#!/usr/bin/env groovy

@Library(value = 'jenkins-sharedlibraries', changelog = false) _

pipeline {
  agent { label 'build-slave-aws' }

  tools {
    jdk 'openjdk17'
    maven 'maven-3.8'
  }

  options {
    buildDiscarder(logRotator(numToKeepStr: "20"))
    timeout(time: 35)
    disableConcurrentBuilds()
    skipDefaultCheckout()
  }

  stages {
    stage('Clean Workspace') {
      steps {
        script {
          if (env.CHANGE_BRANCH) {
            env.BRANCH_NAME = env.CHANGE_BRANCH
          }
        }
        cleanWs()
        checkout scm
      }
    }

    stage('Compile') {
      steps {
        script {
          codePipelineShared.mvnCompile("clean install")
        }
      }

      post {
        always {
          junit allowEmptyResults: true, testResults: '**/target/surefire-reports/*.xml'
        }
      }
    }
  }
}
