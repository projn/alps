pipeline {
  agent any
  stages {
    stage('build') {
      steps {
        sh 'mvn install -Dmaven.test.skip=true'
      }
    }
    stage('code check') {
      steps {
        sh '/opt/software/maven/apache-maven-3.6.0/bin/mvn checkstyle:checkstyle'
      }
    }
    stage('report') {
      steps {
        scanForIssues()
      }
    }
  }
}