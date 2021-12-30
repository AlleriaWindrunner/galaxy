#!/usr/bin/env bash
# -----------------------------------------------------------------------------
# Environment Variable Prerequisites
#
#   LEMON_HOME      Point to the lemon server home directory.
#
#   LEMON_ENV       Should be dev/sit/uat/str/pre/prd.
#
#   JAVA_HOME       Must point at your Java Development Kit installation.
#
#   JAVA_OPTS       (Optional) Java runtime options used when any command
#                   is executed.
#
#   LEMON_BATCH_ENABLED Should be true/false, default false
#
#   LEMON_LOGGER_LEVEL Should be DEBUG/INFO/WARN/ERROR
#
#   APM_OPTS        APM path
# -----------------------------------------------------------------------------

echo " /**                                             "
echo " | **                                            "
echo " | **  /******  /******/****   /******  /******* "
echo " | ** /**__  **| **_  **_  ** /**__  **| **__  **"
echo " | **| ********| ** \ ** \ **| **  \ **| **  \ **"
echo " | **| **_____/| ** | ** | **| **  | **| **  | **"
echo " | **|  *******| ** | ** | **|  ******/| **  | **"
echo " |__/ \_______/|__/ |__/ |__/ \______/ |__/  |__/"

#function defined
offline(){
  PID=$1
  PORT=$(netstat -anp 2>/dev/null|grep -w "${PID}" |grep -w "LISTEN" |awk '{print $4}'|awk -F ":" '{print $NF}')
  #curl --connect-timeout 5 -m 15 -X POST http://127.0.0.1:${PORT}/offline
  httpcode=`curl -I -m 15 -o /dev/null -s -w %{http_code} -X POST http://127.0.0.1:${PORT}/offline`
  if [ ${httpcode} -eq 200 ]; then
     echo
        for wi in {1..15}
        do
          echo -n "."
          sleep 1s
        done
     echo
  fi
}

shutdown(){
  PID=$1
  PORT=$(netstat -anp 2>/dev/null|grep -w "${PID}" |grep -w "LISTEN" |awk '{print $4}'|awk -F ":" '{print $NF}')
  curl --connect-timeout 5 -m 15 -X POST http://127.0.0.1:${PORT}/shutdown
  echo
  for wi in {1..8}
  do
    echo -n "."
    sleep 1s
  done
  echo
}

forceShutdown(){
  kill -9 $1
  for wi in {1..5}
  do
    echo -n "."
    sleep 1s
  done
  echo
}

environmentCheck(){

  if [ -z "${LEMON_ENV}" ]; then
    echo "LEMON_ENV environment variable not defined, value should be dev/sit/uat/str/pre/prd."
    exit 1
  fi

  if [ -z "${JAVA_HOME}" ]; then
    echo "JAVA_HOME environment variable not defined."
    exit 1
  fi

  #Check ulimit -n setting
  ulimit=`ulimit -n`
  if [ ${ulimit} -le 10000 ]; then
     echo "Warning: the ulimit -n ${ulimit} setting is too small"
     exit 1
  fi

}

# Main class of starting lemon server
# LEMON_MAIN="com.galaxy.lemon.Application"

# system env setting
source ~/.bash_profile
ulimit -n 60000
umask 0022
export LANG=zh_CN.utf8

# Lemon home setting
if [ -z "${LEMON_HOME}" ]; then
  #本shell所在的上级目录
  # LEMON_HOME=$HOME
  LEMON_HOME="$(cd `dirname $0`;cd ..;pwd)"
fi

# The home of executable jar
if [ -z "${LEMON_JAR_HOME}" ]; then
  LEMON_JAR_HOME=${LEMON_HOME}/lib
fi

# LEMON_LOGGER_LEVEL default
if [ -z "${LEMON_LOGGER_LEVEL}" ]; then
  LEMON_LOGGER_LEVEL="INFO"
fi

# Get application ID
APPID=$(echo $LEMON_HOME|awk -F "/" '{print $NF}')
echo $APPID

# Check environment variables
environmentCheck

# Executable jar of lemon server
if [ `ls ${LEMON_JAR_HOME}/*.jar|wc -l` -gt 1 ]; then
    echo "Can only have one start jar file in lemon home $LEMON_HOME"
    exit 1
fi

LEMON_JAR=$(ls ${LEMON_JAR_HOME}/*.jar)
if [ ! -f ${LEMON_JAR} ]; then
  LEMON_JAR=${LEMON_HOME}/${LEMON_JAR}
fi
echo $LEMON_JAR

# Set PID file
PID_FILE="${LEMON_HOME}/${APPID}.pid"

# Check if server is running
if [ -f "${PID_FILE}" ]; then
  EXIST_PID=`cat "${PID_FILE}"`
  num=`ps -p "${EXIST_PID}" | grep "${EXIST_PID}" | wc -l`
  if [ ${num} -ge 1 ]; then
    #echo "Can't start Lemon Server server, an existing server[${EXIST_PID}] is running."
    #exit 1
    echo "An existing server[${EXIST_PID}] is running, starting to graceful shutdown it, please wait for a monent."
    # Graceful Shutdown

    offline ${EXIST_PID}
    shutdown ${EXIST_PID}

    num=`ps -p "${EXIST_PID}" | grep "${EXIST_PID}" | wc -l`
    if [ ${num} -ge 1 ]; then
      echo "The server [${EXIST_PID}] has not stoped yet, force shutdown it."
      forceShutdown ${EXIST_PID}
    fi
  fi
fi

# logs settting
LOG_DIR="${LEMON_HOME}/logs"
`mkdir -p "${LOG_DIR}"`
OUT_FILE="${LOG_DIR}/server.out"
ERR_FILE="${LOG_DIR}/server.err"
GC_FILE="${LOG_DIR}/server-gc.log"

# Backup previous logs
#BACK_DIR="${LEMON_HOME}/backup"
#if [ ! -d "${BACK_DIR}" ]; then
#  mkdir -p "${BACK_DIR}"
#fi
#TS=`date +%Y%m%d%H%M%S`
#if [ -d "${LOG_DIR}" ]; then
#  mv "${LOG_DIR}" "${BACK_DIR}/${APPID}_LOG_${TS}"
#fi

# Set options for server starting
MEM_SIZE_MB="512"
MAX_METASPACE_SIZE=256
MEM_OPTS="-Xms${MEM_SIZE_MB}m -Xmx${MEM_SIZE_MB}m -XX:MaxMetaspaceSize=${MAX_METASPACE_SIZE}m"
GC_OPTS="-XX:+UseG1GC -XX:MaxGCPauseMillis=50 -verbose:gc -Xloggc:${GC_FILE} -XX:+PrintGCDateStamps -XX:+PrintGCDetails"
DEBUG_OPTS="-XX:+HeapDumpOnOutOfMemoryError -XX:+UnlockCommercialFeatures -XX:+FlightRecorder"
DEBUG_OPTS="-XX:AutoBoxCacheMax=10000 -XX:+PrintCommandLineFlags ${DEBUG_OPTS}"
JAVA_OPTS="-server ${JAVA_OPTS} ${MEM_OPTS} ${DEBUG_OPTS} ${GC_OPTS} "

CLASSPATH="."
#LIB_DIR="${LEMON_HOME}/lib"
#if [ -d "$LIB_DIR" ]; then
#  for f in ${LIB_DIR}/*.jar
#  do
#    CLASSPATH="${CLASSPATH}:${f}"
#  done
#fi

if [ ! -z "${LEMON_BATCH_ENABLED}" ]; then
  LEMON_BATCH_ENABLE="-Dlemon.batch.enabled=${LEMON_BATCH_ENABLED}"
fi

if [ ! -z "${LEMON_LOGGER_LEVEL}" ]; then
  LEMON_LOGGER_LEVEL_OPT="-Dlemon.logger.level=${LEMON_LOGGER_LEVEL}"
fi


LEMON_OPTS="-Dlemon.env=${LEMON_ENV} -Dlemon.home=${LEMON_HOME} -Dlemon.log.path=${LOG_DIR} ${LEMON_LOGGER_LEVEL_OPT} ${LEMON_BATCH_ENABLE}"
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
echo "APM_OPTS     : ${APM_OPTS}"
echo "--------------------------------------------------"

startTm_s=$(date +%s)
# Start server
#RUN_CMD="${JAVA_HOME}/bin/java ${JAVA_OPTS} -cp ${CLASSPATH} ${LEMON_OPTS} ${LEMON_MAIN} ${SPRING_OPTS}"
RUN_CMD="${JAVA_HOME}/bin/java ${JAVA_OPTS} ${APM_OPTS} ${LEMON_OPTS} -jar ${LEMON_JAR} ${SPRING_OPTS}"
echo "Ready to run Lemon Server with command: " >${OUT_FILE}
echo "${RUN_CMD}" >>${OUT_FILE}
nohup ${RUN_CMD} >>${OUT_FILE} 2>${ERR_FILE} &

# Save PID file
PID=$!
echo ${PID} >"${PID_FILE}"


# Waiting for server starting
echo -n "Waiting for server[${PID}] to start."
start_sec=0
max_sec=180
while [ ${start_sec} -lt ${max_sec} ] ; do
  num=`netstat -anp 2>/dev/null | grep -w "${PID}" | grep -w "LISTEN" | wc -l`
  if [ ${num} -ge 1 ]; then
    endTm_s=$(date +%s)
    execTm=$((endTm_s-startTm_s))
    echo
    PORT=$(netstat -anp 2>/dev/null|grep -w "${PID}" |grep -w "LISTEN" |awk '{print $4}'|awk -F ":" '{print $NF}')
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