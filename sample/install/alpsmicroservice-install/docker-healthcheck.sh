#!/bin/sh

set -e

result=`curl http://127.0.0.1:${SOFTWARE_SERVER_PORT}/actuator/health -s | grep DOWN`
if [ "${result}" != "" ]; then
    exit 1
else
    exit 0
fi
