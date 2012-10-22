#!/bin/bash

PROJECT=./
TOMCAT=~/apache-tomcat-6.0.33/
SETTINGS=~/.troia_settings

JAVA_RESOURCES=$PROJECT/src/main/resources

if [ -d "$SETTINGS" ]; then
    BACKUP=`mktemp -d`
    cp -rf $JAVA_RESOURCES/* $BACKUP/
    cp -rf $SETTINGS/* $JAVA_RESOURCES/
fi

cd $PROJECT
mvn clean
mvn package -DskipTests=true

if [ -d "$SETTINGS" ]; then
    cp -rf $BACKUP/* $JAVA_RESOURCES/
    rm -rf $BACKUP
fi

$TOMCAT/bin/shutdown.sh
rm -rf $TOMCAT/webapps/GetAnotherLabel*
cp $PROJECT/target/*.war $TOMCAT/webapps/
$TOMCAT/bin/startup.sh

