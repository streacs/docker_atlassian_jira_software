# Docker - Atlassian JIRA Software

This is a Docker-Image for Atlassian JIRA Software based on Debian 9.

## Getting started
Run Atlassian JIRA Software standalone and navigate to `http://[dockerhost]:8080` to finish configuration:

```bash
docker run -ti -e ORACLE_JAVA_EULA=accepted -p 8080:8080 streacs/atlassian-jira-software:x.x.x
```

## Environment Variables
* ORACLE_JAVA_EULA
* JVM_ARGUMENTS
* SYSTEM_USER = jira
* SYSTEM_GROUP = jira
* SYSTEM_HOME = /home/jira
* APPLICATION_INST = /opt/atlassian/jira
* APPLICATION_HOME = /var/opt/atlassian/application-data/jira
* TOMCAT_PROXY_NAME =
* TOMCAT_PROXY_PORT =
* TOMCAT_PROXY_SCHEME =
* TOMCAT_PROXY_SECURE =

## Ports
* 8080 = Default HTTP Connector

## Volumes
* /var/opt/atlassian/application-data/jira

## Oracle end user license agreement
To run this container you have to accept the terms of the Oracle Java end user license agreement.
http://www.oracle.com/technetwork/java/javase/terms/license/index.html

Add following environment variable to your configuration : 
```bash
-e ORACLE_JAVA_EULA=accepted
```

## Source Code
[Github](https://github.com/streacs/docker_atlassian_jira_software)
