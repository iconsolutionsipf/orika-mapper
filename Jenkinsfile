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

    stage('Sonarqube') {
      environment {
        SCANNER_HOME = tool 'SonarQubeScanner'
      }

      steps {
        script {
          codePipelineShared.sonarqubeScan("orika")
        }
      }
    }

    stage('NexusIQ') {
      steps {
        script {
          codePipelineShared.nexusIqScan("orika")
          codePipelineShared.nexusIqGetReport("orika", 'build', 'components')
        }
      }
    }

    stage('Quality Gate - Coverage') {
      steps {
        script {
          waitForQualityGate abortPipeline: true
          codePipelineShared.coverageScan(80)
        }
      }
    }

    stage('Quality Gate - Bugs/Vulnerabilities') {
      steps {
        script {
          codePipelineShared.sonarqubeVulnerabilities('orika', env.BRANCH_NAME)
        }
      }
    }
  }
}
