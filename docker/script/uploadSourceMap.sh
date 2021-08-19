#!/bin/sh

set -e

echo "Execute: curl -s \"${FRONT}/${FILE}.map\"\` > /tmp/map"

dataRes=`curl -s -o /dev/null -w "%{http_code}" "${FRONT}/${FILE}.map"`
if [ ${dataRes} = "200" ]
then
  echo "front is up"
  data=`curl -s "${FRONT}/${FILE}.map"`
  echo "${data}" > /tmp/map
else
  echo "Fail: status ${dataRes}"
  exit 1
fi


echo "Execute: curl -s -o /dev/null -w \"%{http_code}\" -XPOST  ${APM}/assets/v1/sourcemaps -F sourcemap=\"@/tmp/map\" -F service_version=\"1.0.0\" -F bundle_filepath=\"${FRONT}/${FILE}\" -F service_name=\"angular-app\""

result=`curl -o /dev/null -w "%{http_code}" -XPOST  ${APM}/assets/v1/sourcemaps -F sourcemap="@/tmp/map" -F service_version="1.0.0" -F bundle_filepath="${FRONT}/${FILE}" -F service_name="angular-app"`

echo "HTTP result code is ${result}"

if [ ${result} = "202" ]
then
  echo "Success"
  exit 0
else
  echo "Fail: status ${result}"
  exit 1
fi

