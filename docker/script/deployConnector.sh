#!/bin/sh

set -e

# Read command line
if [ "$#" -eq 3 ]
then
  url=${1}
  name=${2}
  file=${3}
else
  echo "Usage: ${0} <url-kafka-conect:port> <connectorName> <pathToJsonConfig>" >&2
  echo "      exemple: ${0} http://localhost:8083 connectorES /tmp/body.json" >&2
  exit 1
fi

echo "Execute: curl -s -o /dev/null -w \"%{http_code}\" -XPOST -H \"Content-Type: application/json\" ${url}/connectors --data \"@${file}\""

result=`curl -s -o /dev/null -w "%{http_code}" -XPOST -H "Content-Type: application/json" ${url}/connectors --data "@${file}"`

#echo "HTTP result code is ${result}"

if [ ${result} = "201" ]
then
  echo "Success"
  exit 0
elif [ ${result} = "409" ]
then
  echo "Connector already deployed"
  # TODO: remove connector and force redeploy
  exit 0
else
  echo "Fail: status ${result}"
  exit 1
fi

