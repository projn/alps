#!/bin/bash

##########################################################
# 初始化开发项目环境                                        #
# Author: sunyuecheng                                    #
##########################################################

source ./VERSION

mvn install:install-file -Dfile=./alps-bean-${MODULE_VERSION}.jar -DgroupId=com.projn.alps -DartifactId=alps-bean -Dversion=${MODULE_VERSION} -Dpackaging=jar
mvn install:install-file -Dfile=./alps-dao-${MODULE_VERSION}.jar -DgroupId=com.projn.alps -DartifactId=alps-dao -Dversion=${MODULE_VERSION} -Dpackaging=jar
mvn install:install-file -Dfile=./alps-test-${MODULE_VERSION}.jar -DgroupId=com.projn.alps -DartifactId=alps-test -Dversion=${MODULE_VERSION} -Dpackaging=jar
mvn install:install-file -Dfile=./alps-common-${MODULE_VERSION}.jar -DgroupId=com.projn.alps -DartifactId=alps-common -Dversion=${MODULE_VERSION} -Dpackaging=jar
