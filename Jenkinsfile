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
            sh 'mvn checkstyle:checkstyle-aggregate'
          }
        }
        stage('find bugs') {
          steps {
            sh 'mvn findbugs:findbugs'
          }
        }
        stage('pmd') {
          steps {
            sh 'mvn pmd:pmd'
          }
        }
      }
    }
    stage('package') {
      steps {
        sh 'source ./VERSION;tar -czf alpsgenerator-${ALPS_GENERATOR_VERSION}.tar.gz ./intsall/alpsgenerator'
      }
    }
  }
}