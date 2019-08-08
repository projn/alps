#!/bin/sh

set -e

log_file_path=${SOFTWARE_LOG_PATH}/run.log
touch ${log_file_path} || { echo "Can not write to ${log_file_path}. Wrong volume permissions?"; exit 1; }
echo "Docker run at $(date)" >> ${log_file_path}

if [ $# -eq 2 ]; then
    echo "spring.cloud.consul.host=${1}" >> ${SERVICE_INSTALL_PATH}/context/bootstrap.properties
    echo "spring.cloud.consul.port=${2}" >> ${SERVICE_INSTALL_PATH}/context/bootstrap.properties

    exec java -jar -Dconfig.dir=${SERVICE_INSTALL_PATH}/context ${SERVICE_INSTALL_PATH}/alpsmicroservice-0.0.1.jar --spring.config.location=${SERVICE_INSTALL_PATH}/context/bootstrap.properties ${SERVICE_INSTALL_PATH}/module
fi

exec "$@"
