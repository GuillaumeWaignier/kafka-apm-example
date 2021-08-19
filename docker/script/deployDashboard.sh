#!/bin/sh

set -e

# Read command line
if [ "$#" -eq 2 ]
then
  url=${1}
  file=${2}
else
  echo "Usage: ${0} <url-kibana:port> <pathToSavedJsonObject>" >&2
  echo "      exemple: ${0} http://kibana:5601 /kibana.ndjson" >&2
  exit 1
fi

echo "Execute: curl -s -o /dev/null -w \"%{http_code}\" -XPOST -H \"kbn-xsrf: true\" ${url}/api/saved_objects/_import --form file=@${file}"

result=`curl -s -o /dev/null -w "%{http_code}" -XPOST -H "kbn-xsrf: true" ${url}/api/saved_objects/_import --form file=@${file}`

if [ ${result} = "200" ]
then
  echo "Success"
  exit 0
else
  echo "Fail: status ${result}"
  exit 1
fi
