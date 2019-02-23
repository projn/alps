pipeline {
  agent any
  stages {
    stage('build') {
      steps {
        sh 'mvn install -Dmaven.test.skip=true'
      }
    }
    stage('code check') {
      parallel {
        stage('check style') {
          steps {
            sh 'mvn checkstyle:checkstyle'
          }
        }
        stage('find bugs') {
          steps {
            sh 'mvn findbugs:findbugs'
          }
        }
        stage('pdm') {
          steps {
            sh 'mvn pdm:pdm'
          }
        }
      }
    }
    stage('report') {
      steps {
        scanForIssues()
      }
    }
  }
}