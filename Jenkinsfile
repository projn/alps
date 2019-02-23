pipeline {
  agent any
  stages {
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
        stage('pmd') {
          steps {
            sh 'mvn pmd:pmd'
          }
        }
        stage('cpd') {
          steps {
            sh 'mvn pmd:cpd'
          }
        }
      }
    }
    stage('build') {
      steps {
        sh 'mvn install'
      }
    }
    stage('package') {
      steps {
        sh '''source ./VERSION; \\
tar -czf ./target/alpsgenerator-${ALPS_GENERATOR_VERSION}.tar.gz ./target/alpsgenerator'''
      }
    }
    stage('report') {
      steps {
        junit '**/target/*-reports/TEST-*.xml'
        recordIssues(enabledForFailure: true, tool: checkStyle(pattern: 'target/checkstyle.xml'), sourceCodeEncoding: 'UTF-8')
        recordIssues(enabledForFailure: true, tool: cpd(pattern: 'target/cpd.xml'), sourceCodeEncoding: 'UTF-8')
        recordIssues(enabledForFailure: true, tool: pmdParser(pattern: 'target/pmd.xml'), sourceCodeEncoding: 'UTF-8')
        recordIssues(enabledForFailure: true, tool: findBugs(pattern: 'target/findbugsXml.xml'), sourceCodeEncoding: 'UTF-8')
      }
    }
  }
}