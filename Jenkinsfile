pipeline {
  agent none
  stages {
    stage('build') {
      steps {
        sh '/opt/software/maven/apache-maven-3.6.0/bin/mvn checkstyle:checkstyle'
      }
    }
    stage('report') {
      steps {
        readFile(file: 'target/site/checkstyle-aggregate.html', encoding: 'utf-8')
      }
    }
  }
}