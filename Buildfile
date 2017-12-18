#!/bin/bash

DOCKER_REPOSITORY="streacs"
APPLICATION_NAME="atlassian-jira-software"

function build_container {
      docker build --no-cache -t ${DOCKER_REPOSITORY}/${APPLICATION_NAME}:latest .
}

function remove_container {
      docker rmi ${DOCKER_REPOSITORY}/${APPLICATION_NAME}:latest
}

case $1 in
  package)
    build_container
  ;;
  test)
    build_container
    remove_container
  ;;
  *)
    echo "No valid arguments provided (package)"
    exit 1
esac