#!/bin/bash

PROJECT=./
TOMCAT=/var/lib/tomcat6
SETTINGS=~/.troia_settings

JAVA_RESOURCES=$PROJECT/src/main/resources

if [ -d "$SETTINGS" ]; then
    BACKUP=`mktemp -d`
    cp -rf $JAVA_RESOURCES/* $BACKUP/
    cp -rf $SETTINGS/* $JAVA_RESOURCES/
fi

cd core
mvn clean
mvn install -DskipTests=true
cd ../service
mvn clean
mvn package -DskipTests=true

if [ -d "$SETTINGS" ]; then
    cp -rf $BACKUP/* $JAVA_RESOURCES/
    rm -rf $BACKUP
fi

sudo service tomcat6 stop
sudo rm -rf $TOMCAT/webapps/service-1.1*
sudo cp ./target/*.war $TOMCAT/webapps/
sudo service tomcat6 start
curl -X POST -d "" "http://localhost:8080/service-1.1/config"

