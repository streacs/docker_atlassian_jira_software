#!/bin/bash

DOCKER_REPOSITORY="streacs"
APPLICATION_NAME="atlassian-jira-software"

APPLICATION_RSS="https://my.atlassian.com/download/feeds/jira-software.rss"

APPLICATION_BRANCH="$(git symbolic-ref --short HEAD)"

function build_container {
  case $APPLICATION_BRANCH in
    master)
      APPLICATION_RELEASE="$(wget -qO- ${APPLICATION_RSS} | grep -o -E "(\d{1,2}\.){2,3}\d" | uniq)"
      echo "Building MASTER with RELEASE $APPLICATION_RELEASE"
      docker build --no-cache -t ${DOCKER_REPOSITORY}/${APPLICATION_NAME}:master --build-arg APPLICATION_RELEASE=${APPLICATION_RELEASE} .
    ;;
    develop)
      APPLICATION_RELEASE="$(wget -qO- ${APPLICATION_RSS} | grep -o -E "(\d{1,2}\.){2,3}\d" | uniq)"
      echo "Building DEVELOP with RELEASE $APPLICATION_RELEASE"
      docker build --no-cache -t ${DOCKER_REPOSITORY}/${APPLICATION_NAME}:develop --build-arg APPLICATION_RELEASE=${APPLICATION_RELEASE} .
    ;;
    release*)
      APPLICATION_RELEASE="$(git symbolic-ref --short HEAD | grep -o -E "(\d{1,2}\.){2,3}\d")"
      echo "Building RELEASE with RELEASE $APPLICATION_RELEASE"
      docker build --no-cache -t ${DOCKER_REPOSITORY}/${APPLICATION_NAME}:${APPLICATION_RELEASE} --build-arg APPLICATION_RELEASE=${APPLICATION_RELEASE} .
    ;;
    feature*)
      APPLICATION_RELEASE="$(wget -qO- ${APPLICATION_RSS} | grep -o -E "(\d{1,2}\.){2,3}\d" | uniq)"
      echo "Building FEATURE with BRANCH $APPLICATION_BRANCH and RELEASE $APPLICATION_RELEASE"
      docker build --no-cache -t ${DOCKER_REPOSITORY}/${APPLICATION_NAME}:feature --build-arg APPLICATION_RELEASE=${APPLICATION_RELEASE} .
    ;;
    *)
      echo "No match found"
    ;;
  esac
}

function test_container {
  case $APPLICATION_BRANCH in
    master)
      docker run -ti --rm --env-file files/environment.list ${DOCKER_REPOSITORY}/${APPLICATION_NAME}:master rake spec /home/jira/spec
    ;;
    develop)
      docker run -ti --rm --env-file files/environment.list ${DOCKER_REPOSITORY}/${APPLICATION_NAME}:develop rake spec /home/jira/spec
    ;;
    release*)
      APPLICATION_RELEASE="$(git symbolic-ref --short HEAD | grep -o -E "(\d{1,2}\.){2,3}\d")"
      docker run -ti --rm --env-file files/environment.list ${DOCKER_REPOSITORY}/${APPLICATION_NAME}:${APPLICATION_RELEASE} rake spec /home/jira/spec
    ;;
    feature*)
      docker run -ti --rm --env-file files/environment.list ${DOCKER_REPOSITORY}/${APPLICATION_NAME}:feature rake spec /home/jira/spec
    ;;
    *)
      echo "No match found"
    ;;
  esac
}

function remove_container {
  case $APPLICATION_BRANCH in
    master)
      docker rmi ${DOCKER_REPOSITORY}/${APPLICATION_NAME}:master
    ;;
    develop)
      docker rmi ${DOCKER_REPOSITORY}/${APPLICATION_NAME}:develop
    ;;
    release*)
      APPLICATION_RELEASE="$(git symbolic-ref --short HEAD | grep -o -E "(\d{1,2}\.){2,3}\d")"
      docker rmi ${DOCKER_REPOSITORY}/${APPLICATION_NAME}:${APPLICATION_RELEASE}
    ;;
    feature*)
      docker rmi ${DOCKER_REPOSITORY}/${APPLICATION_NAME}:feature
    ;;
    *)
      echo "No match found"
    ;;
  esac
}

case $1 in
  package)
    build_container
    test_container
    remove_container
  ;;
  build)
    build_container
  ;;
  test)
    test_container
  ;;
  remove)
    remove_container
  ;;
  *)
    echo "No valid arguments provided (package)"
    exit 1
esac