#!/bin/sh

set -e

log_file_path=${SOFTWARE_LOG_PATH}/run.log
touch ${log_file_path} || { echo "Can not write to ${log_file_path}. Wrong volume permissions?"; exit 1; }
echo "Docker run at $(date)" >> ${log_file_path}

if [ $# -eq 2 ]; then
    export SOFTWARE_CONSUL_SERVER_ADDRESS=${1}
    export SOFTWARE_CONSUL_PORT=${2}
fi

exec java -jar -Dconfig.dir=./context alpsmicroservice-1.0.1.jar --spring.config.location=./context/bootstrap.properties ./module

