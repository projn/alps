pipeline {
  agent any
  stages {
    stage('build') {
      steps {
        sh 'mvn checkstyle:checkstyle'
      }
    }
    stage('report') {
      steps {
        readFile(file: 'target/site/checkstyle-aggregate.html', encoding: 'utf-8')
      }
    }
  }
}