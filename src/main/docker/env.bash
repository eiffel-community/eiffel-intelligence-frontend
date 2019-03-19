#!/usr/bin/env bash

if [[ "${DOCKER_HOST}" ]]; then
  export HOST=$(echo ${DOCKER_HOST} | sed -e 's|^tcp\://||;s|:.*$||');
else
  export HOST=$(hostname -I | tr " " "\n"| head -1);
  echo "Docker Host IP: $HOST"
fi

export EIFFEL2_EI_FRONTEND_EI_INSTANCES_LIST="[\
{ \"contextPath\": \"\", \"port\": \"8080\", \"name\": \"ei-backend-artifact\", \"host\": \"ei-backend-artifact\", \"https\": false, \"defaultBackend\": true},\
{ \"contextPath\": \"\", \"port\": \"8080\", \"name\": \"ei-backend-sourcechange\", \"host\": \"ei-backend-sourcechange\", \"https\": false, \"defaultBackend\": false},\
{ \"contextPath\": \"\", \"port\": \"8080\", \"name\": \"ei-backend-testexecution\", \"host\": \"ei-backend-testexecution\", \"https\": false, \"defaultBackend\": false}\
]"

