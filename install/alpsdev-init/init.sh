#!/bin/bash

##########################################################
# 初始化开发项目环境                                        #
# Author: sunyuecheng                                    #
##########################################################

source ./VERSION

# nexus_repo_url like 'http://192.168.37.XXX:8082'
nexus_repo_url=''

src='<module>alpsmicroservice</module>'
dst=''
sed -i "s#$src#$dst#g" pom.xml

src='<module>alpsgenerator</module>'
dst=''
sed -i "s#$src#$dst#g" pom.xml

src='<module>alpsconfigserver</module>'
dst=''
sed -i "s#$src#$dst#g" pom.xml

if [ "$nexus_repo_url" != "" ]; then
    src='</project>'
    dst="<distributionManagement>\n        <repository>\n            <id>nexus-releases</id>\n            <name>Nexus Release Repository</name>\n            <url>${nexus_repo_url}/repository/maven-releases/</url>\n        </repository>\n        <snapshotRepository>\n            <id>nexus-snapshots</id>\n            <name>Nexus Snapshot Repository</name>\n            <url>${nexus_repo_url}/repository/maven-snapshots/</url>\n        </snapshotRepository>\n    </distributionManagement>\n</project>"
    sed -i "s#$src#$dst#g" pom.xml

    mvn deploy
else
    mvn install
    #mvn install:install-file -Dfile=./alps-bean/target/alps-bean-${ALPS_MODULE_VERSION}.jar -DgroupId=com.projn.alps -DartifactId=alps-bean -Dversion=${ALPS_MODULE_VERSION} -Dpackaging=jar
fi

