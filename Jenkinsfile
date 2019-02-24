pipeline {
  agent any
  options {
    timeout(time: 1, unit: 'HOURS')
  }

  stages {
    stage('build') {
      steps {
        sh 'mvn clean install'
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
        stage('pmd') {
          steps {
            sh 'mvn pmd:pmd'
          }
        }
        stage('pmd') {
          steps {
            sh 'mvn pmd:cpd'
          }
        }
        stage('test') {
           steps {
             sh 'mvn surefire-report:report'
           }
        }
      }
    }

    stage('report') {
      parallel {
        stage('test report') {
          steps {
            junit testResults: '**/target/*-reports/TEST-*.xml'
          }
        }
        stage('maven report') {
          steps {
            recordIssues enabledForFailure: true, tool: mavenConsole()
            recordIssues enabledForFailure: true, tools: [java(), javaDoc()], sourceCodeEncoding: 'UTF-8'
          }
        }
        stage('checkstyle report') {
          steps {
            recordIssues enabledForFailure: true, tool: checkStyle(pattern: 'target/checkstyle.xml'), sourceCodeEncoding: 'UTF-8'
          }
        }
        stage('pmd report') {
          steps {
            recordIssues enabledForFailure: true, tool: cpd(pattern: 'target/cpd.xml'), sourceCodeEncoding: 'UTF-8'
            recordIssues enabledForFailure: true, tool: pmdParser(pattern: 'target/pmd.xml'), sourceCodeEncoding: 'UTF-8'
          }
        }
        stage('findbugs report') {
          steps {
            recordIssues enabledForFailure: true, tool: findBugs(pattern: 'target/findbugsXml.xml'), sourceCodeEncoding: 'UTF-8'
          }
        }
      }
    }

    stage('release') {
      parallel {
        stage('config server ') {
          steps {
            sh '''cd ./target;
git clone https://github.com/projn/popigai.git; \\
rm -rf popigai/instal/alpsconfigserver-install; \\
cp -r ../install/alpsconfigserver-install popigai/instal/; \\
git add popigai/instal/alpsconfigserver-install; \\
git commit -m "Jenkins auto commit alps config server intsall package"; \\
git push; \\
source ./VERSION; \\
cp ./alpsconfigserver/target/*.jar ~/release/configserver'''
          }
        }

        stage('generator') {
          steps {
            sh '''source ./VERSION; \\
tar -czf ~/release/generator/alpsgenerator-${ALPS_GENERATOR_VERSION}.tar.gz ./target/alpsgenerator'''
          }
        }
      }
    }
  }
}