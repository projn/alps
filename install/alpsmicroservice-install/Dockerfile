# escape=\

ARG  BASE_IMAGE_VERSION=8u191-jdk-alpine3.9
FROM openjdk:${BASE_IMAGE_VERSION}

LABEL author="projn"
LABEL version="1.0"
LABEL description="This is a demo for docker build."

ENV LANG C.UTF-8
ENV SOFTWARE_INSTALL_PATH /opt/software/alpsmicroservice
ENV SOFTWARE_LOG_PATH /var/log/alpsmicroservice

ARG SOFTWARE_SERVER_IP=0.0.0.0
ENV SOFTWARE_SERVER_PORT 5443

RUN mkdir -p ${SOFTWARE_INSTALL_PATH}
RUN chmod u=rwx,g=rx,o=r ${SOFTWARE_INSTALL_PATH}

RUN mkdir -p ${SOFTWARE_LOG_PATH}
RUN chmod u=rwx,g=r,o=r ${SOFTWARE_LOG_PATH}

WORKDIR ${SOFTWARE_INSTALL_PATH}

COPY ./alpsmicroservice ./
ADD ./config/cloud/bootstrap.properties ./context/
COPY ./docker-entrypoint.sh ./
COPY ./docker-healthcheck.sh ./

RUN chmod -R u=rwx,g=rx,o=r ./

RUN sed -i "s#SOFTWARE_LOG_PATH#${SOFTWARE_LOG_PATH}#g" ./context/bootstrap/log4j2-config.xml
RUN sed -i "s#SOFTWARE_SERVER_NAME#${SOFTWARE_SERVER_NAME}#g" ./context/bootstrap.properties
RUN sed -i "s#SOFTWARE_SERVER_IP#${SOFTWARE_SERVER_IP}#g" ./context/bootstrap.properties
RUN sed -i "s#SOFTWARE_SERVER_PORT#${SOFTWARE_SERVER_PORT}#g" ./context/bootstrap.properties

RUN sed -i "s/spring.cloud.consul.host/#spring.cloud.consul.host/g" ./context/bootstrap.properties
RUN sed -i "s/spring.cloud.consul.port/#spring.cloud.consul.port/g" ./context/bootstrap.properties

EXPOSE ${SOFTWARE_SERVER_PORT}/tcp
VOLUME ${SOFTWARE_LOG_PATH}

HEALTHCHECK --interval=30s --timeout=10s --retries=3 CMD /bin/bash ${SOFTWARE_INSTALL_PATH}/docker-healthcheck.sh

ENTRYPOINT ["./docker-entrypoint.sh"]

CMD ["config-server", "8080"]