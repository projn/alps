pipeline {
  agent any
  stages {
    stage('build') {
      steps {
        sh '/opt/software/maven/apache-maven-3.6.0/bin/mvn install -Dmaven.test.skip=true'
      }
    }
    stage('check') {
      steps {
        sh '/opt/software/maven/apache-maven-3.6.0/bin/mvn checkstyle:checkstyle'
      }
    }
  }
}