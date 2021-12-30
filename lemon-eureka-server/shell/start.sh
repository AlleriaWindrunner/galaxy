#!/usr/bin/env bash
# -----------------------------------------------------------------------------
# Environment Variable Prerequisites
#
#   LEMON_HOME      Point to the lemon server home directory.
#
#   LEMON_ENV       Should be DEV/CI/SIT/UAT/STR/PRE/PRD.
#
#   JAVA_HOME       Must point at your Java Development Kit installation.
#
#   JAVA_OPTS       (Optional) Java runtime options used when any command
#                   is executed.
# -----------------------------------------------------------------------------

echo " /**                                             "
echo " | **                                            "
echo " | **  /******  /******/****   /******  /******* "
echo " | ** /**__  **| **_  **_  ** /**__  **| **__  **"
echo " | **| ********| ** \ ** \ **| **  \ **| **  \ **"
echo " | **| **_____/| ** | ** | **| **  | **| **  | **"
echo " | **|  *******| ** | ** | **|  ******/| **  | **"
echo " |__/ \_______/|__/ |__/ |__/ \______/ |__/  |__/"

# Main class of lemon server
# LEMON_MAIN="com.cmpay.lemon.Application"
# Executable jar of lemon server
LEMON_JAR=""

# Check environment variables
if [ -z "${LEMON_HOME}" ]; then
  LEMON_HOME="$(cd `dirname $0`;cd ..;pwd)"
fi
if [ -z "${LEMON_JAR}" ]; then
  JARS_IN_HOME=$(ls ${LEMON_HOME}/*.jar)
  _i=0
  for JAR in $JARS_IN_HOME
  do
    _i=$((_i + 1))
    echo "Deploying ${JAR}...."
    if [ ${_i} -gt 1 ]; then
      echo "Can only have one start jar file in lemon home $LEMON_HOME"
      exit 1
    fi
    LEMON_JAR=$JAR
  done
fi

# Get application ID
APPID=$(echo $LEMON_HOME|awk -F "/" '{print $NF}')
echo $APPID


if [ ! -f ${LEMON_JAR} ]; then
  LEMON_JAR=${LEMON_HOME}/${LEMON_JAR}
fi

if [ -z "${LEMON_ENV}" ]; then
  echo "LEMON_ENV environment variable not defined, value should be DEV/CI/SIT/UAT/STR/PRE/PRD."
  exit 1
fi
if [ -z "${JAVA_HOME}" ]; then
  JAVA_PATH=`which java 2>/dev/null`
  if [ "x${JAVA_PATH}" != "x" ]; then
    JAVA_HOME=`dirname $(dirname ${JAVA_PATH}) 2>/dev/null`
  fi
  if [ -z "${JAVA_HOME}" ]; then
    echo "JAVA_HOME environment variable not defined."
    exit 1
  fi
fi

# Set PID file
PID_FILE="${LEMON_HOME}/${APPID}.pid"

# Check if server is running
if [ -f "${PID_FILE}" ]; then
  EXIST_PID=`cat "${PID_FILE}"`
  num=`ps -p "${EXIST_PID}" | grep "${EXIST_PID}" | wc -l`
  if [ ${num} -ge 1 ]; then
    #echo "Can't start Lemon Server server, an existing server[${EXIST_PID}] is running."
    #exit 1
    echo "An existing server[${EXIST_PID}] is running, starting to kill it, please wait for a moment."
    kill -9 ${EXIST_PID}
    for wi in {1..5}
    do
      echo -n "."
      sleep 1s
    done
    echo
  fi
fi

# Backup previous logs
LOG_DIR="${LEMON_HOME}/logs"
BACK_DIR="${LEMON_HOME}/backup"
if [ ! -d "${BACK_DIR}" ]; then
  mkdir -p "${BACK_DIR}"
fi
TS=`date +%Y%m%d%H%M%S`
if [ -d "${LOG_DIR}" ]; then
  mv "${LOG_DIR}" "${BACK_DIR}/${APPID}_LOG_${TS}"
fi

# Log files
`mkdir -p "${LOG_DIR}"`
OUT_FILE="${LOG_DIR}/server.out"
ERR_FILE="${LOG_DIR}/server.err"
GC_FILE="${LOG_DIR}/server-gc.log"

# Set options for server starting
MEM_SIZE_MB="512"
MEM_OPTS="-Xms${MEM_SIZE_MB}m -Xmx${MEM_SIZE_MB}m"
GC_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=50 -verbose:gc -Xloggc:${GC_FILE} -XX:+PrintGCDateStamps -XX:+PrintGCDetails"
DEBUG_OPTS="-Xverify:none -XX:+HeapDumpOnOutOfMemoryError -XX:+UnlockCommercialFeatures -XX:+FlightRecorder"
DEBUG_OPTS="-XX:AutoBoxCacheMax=10000 -XX:+PrintCommandLineFlags ${DEBUG_OPTS}"
JAVA_OPTS="${JAVA_OPTS} ${MEM_OPTS} ${DEBUG_OPTS} ${GC_OPTS}"

CLASSPATH="."
#LIB_DIR="${LEMON_HOME}/lib"
#if [ -d "$LIB_DIR" ]; then
#  for f in ${LIB_DIR}/*.jar
#  do
#    CLASSPATH="${CLASSPATH}:${f}"
#  done
#fi

LEMON_OPTS="-Dlemon.env=${LEMON_ENV} -Dlemon.home=${LEMON_HOME} -Dlemon.log.path=${LOG_DIR} -Deureka.server.host=${EUREKA_SERVER_HOST}"

SPRING_OPTS_ACTIVE="--spring.profiles.active=${LEMON_ENV}"
SPRING_OPTS="${SPRING_OPTS_ACTIVE}"

echo "--------------------------------------------------"
echo "Starting Lemon Server "
echo "--------------------------------------------------"
echo "LEMON_HOME   : ${LEMON_HOME}"
echo "LEMON_ENV    : ${LEMON_ENV}"
echo "LEMON_JAR   : ${LEMON_JAR}"
#echo "LEMON_MAIN   : ${LEMON_MAIN}"
echo "LEMON_OPTS : ${LEMON_OPTS}"
echo "SPRING_OPTS : ${SPRING_OPTS}"
echo "LOG_DIR : ${LOG_DIR}"
echo "JAVA_HOME    : ${JAVA_HOME}"
echo "JAVA_OPTS    : ${JAVA_OPTS}"
echo "--------------------------------------------------"

startTm_s=$(date +%s)
# Start server
#RUN_CMD="${JAVA_HOME}/bin/java ${JAVA_OPTS} -cp ${CLASSPATH} ${LEMON_OPTS} ${LEMON_MAIN} ${SPRING_OPTS}"
RUN_CMD="${JAVA_HOME}/bin/java ${JAVA_OPTS} ${LEMON_OPTS} -jar ${LEMON_JAR} ${SPRING_OPTS}"
echo "Ready to run Lemon Server with command: " >${OUT_FILE}
echo "${RUN_CMD}" >>${OUT_FILE}
nohup ${RUN_CMD} >>${OUT_FILE} 2>${ERR_FILE} &

# Save PID file
PID=$!
echo ${PID} >"${PID_FILE}"


# Waiting for server starting
echo -n "Waiting for server[${PID}] to start."
start_sec=0
max_sec=60
while [ ${start_sec} -lt ${max_sec} ] ; do
  num=`ss -anp 2>/dev/null | grep -w "${PID}" | grep -w "LISTEN" | wc -l`
  if [ ${num} -ge 1 ]; then
    endTm_s=$(date +%s)
    execTm=$((endTm_s-startTm_s))
    echo
    PORT=$(ss -anp 2>/dev/null|grep -w "${PID}" |grep -w "LISTEN" |awk '{print $5}'|awk -F ":" '{print $NF}')
    echo "Lemon started on port(s): ${PORT}"
    echo "Started Lemon Application in ($execTm) seconds "
    exit 0
  fi
  echo -n "."
  min=`expr ${start_sec} + 1`
  sleep 1
done
echo "Server did not started in ${max_sec} seconds, please check log files."
exit 1

