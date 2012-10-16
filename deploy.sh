#!/bin/bash

PROJECT=./
TOMCAT=~/apache-tomcat-6.0.33/

cd $PROJECT
mvn clean
mvn package -DskipTests=true

$TOMCAT/bin/shutdown.sh
rm -rf $TOMCAT/webapps/GetAnotherLabel*
cp $PROJECT/target/*.war $TOMCAT/webapps/
$TOMCAT/bin/startup.sh

