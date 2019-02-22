pipeline {
  agent any
  stages {
    stage('Check') {
      steps {
        sh 'mvn checkstyle:checkstyle'
      }
    }
  }
}