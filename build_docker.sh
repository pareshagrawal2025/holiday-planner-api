#!/usr/bin/env bash

# docker must be installed in the system
# This script will build docker image for holiday-planner-api service
# You can change the image name and version as per your requirement
# Example: holidayplanner.example.com/api/holiday-planner-api:0.0.1-SNAPSHOT
# In case if you want to build docker specific image run this script from project root folder

SCRIPT_DIR=`dirname $0`
[ "$SCRIPT_DIR" = "." ] && SCRIPT_DIR=`cd . && pwd`

DOCKER=`which docker`

if [ -f ${DOCKER} ]; then
  ${DOCKER} build -t holidayplanner.example.com/api/holiday-planner-api:0.0.1-SNAPSHOT .
else
  echo "docker not found in this system"
fi
