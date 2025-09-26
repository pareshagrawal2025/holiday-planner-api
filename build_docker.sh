#!/usr/bin/env bash

# docker must be installed in the system to run this script
# This script must be run from the project root folder
# Example: /home/user/holiday-planner-api
# This script will build docker image for holiday-planner-api service with tag 2.1.0
# The image name and version can be changed as per the requirement
# Example image name after successful build: holidayplanner.example.com/api/holiday-planner-api:2.1.0

SCRIPT_DIR=`dirname $0`
[ "$SCRIPT_DIR" = "." ] && SCRIPT_DIR=`cd . && pwd`

DOCKER=`which docker`

if [ -f ${DOCKER} ]; then
  ${DOCKER} build -t holidayplanner.example.com/api/holiday-planner-api:2.1.0 .
else
  echo "docker not found in this system"
fi
