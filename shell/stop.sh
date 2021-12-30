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



# Main class of stopping lemon server
# LEMON_MAIN="com.galaxy.lemon.Application"

# Lemon home setting
if [ -z "${LEMON_HOME}" ]; then
  #本shell所在的上级目录
  # LEMON_HOME=$HOME
  LEMON_HOME="$(cd `dirname $0`;cd ..;pwd)"
fi

# Get application ID
APPID=$(echo $LEMON_HOME|awk -F "/" '{print $NF}')
echo $APPID

# Set PID file
PID_FILE="${LEMON_HOME}/${APPID}.pid"

# Check if server is running
if [ -f "${PID_FILE}" ]; then
  EXIST_PID=`cat "${PID_FILE}"`
  num=`ps -p "${EXIST_PID}" | grep "${EXIST_PID}" | wc -l`
  if [ ${num} -ge 1 ]; then
    #echo "Can't start Lemon Server server, an existing server[${EXIST_PID}] is running."
    #exit 1
    echo "An existing server[${EXIST_PID}] is running, starting to graceful shutdown it, please wait for a moment."
    # Graceful Shutdown
    offline ${EXIST_PID}
    shutdown ${EXIST_PID}

    num=`ps -p "${EXIST_PID}" | grep "${EXIST_PID}" | wc -l`
    if [ ${num} -ge 1 ]; then
      echo "The server [${EXIST_PID}] has not stopped yet, force shutdown it."
      forceShutdown ${EXIST_PID}
    fi
  fi
fi
