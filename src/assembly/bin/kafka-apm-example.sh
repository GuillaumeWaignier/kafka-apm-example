#!/bin/sh

base_dir=$(dirname $0)

variable_prefix="KAFKATRACE_"

IFS="
"

# Read command line
if [ "$#" -eq 0 ]
then
  config_file=${base_dir}/../config/configFromEnv.properties
  use_env=1
elif [ "$#" -eq 1 ]
then
  config_file=${1}
  use_env=0
else
  echo "Usage: ${0} <path_to_kafka_config.properties>" >&2
  echo "   Or export your configuration in env variable prefixed with ${variable_prefix}" >&2
  echo "      exemple: export ${variable_prefix}BOOTSTRAP_SERVERS=localhost:9092" >&2
  exit 1
fi


# Create config file
if [ "${use_env}" ]
then
  echo "#Generated config file" > ${config_file}
  for param in `env`
  do
    case ${param} in
      ${variable_prefix}* )
        key=`echo ${param} | sed -e "s/^${variable_prefix}\([A-Za-z_0-9-]*\)=.*/\1/g" | sed -e 's/_/./g' `
        value=`echo ${param} | sed -e "s/^${variable_prefix}[A-Za-z_0-9-]*=\(.*\)/\1/g" `
        echo ${key}=${value} >> ${config_file}
      ;;
    esac
  done
fi

# Create the classpath
for jar in `ls ${base_dir}/../lib`
do
  classpath="${base_dir}/../lib/${jar}:${classpath}"
done

# Execute
exec java ${JAVA_OPTION} ${JAVA_HEAP} -cp ${classpath} org.ianitrix.kafka.apm.example.Main ${config_file}
