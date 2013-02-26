#!/bin/bash

#URL="http://localhost:8080/troia-server-1.0"
URL="http://project-troia.com/api"
redirectId=0

function createJob 
{
  local result=$(curl -s1 -X POST -H "Content-Type: application/json" "$URL/cjobs")
  local status=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  while [[ $status != "OK" ]]
    do
      sleep 5 
      result=$(curl -s1 -X POST -H "Content-Type: application/json" "$URL/cjobs")
      status=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
    done
  local jid=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 3 | cut -d '"' -f 1)
  echo "$jid"
}


function deleteJob 
{
  echo "Deleting job with ID $1 ..."
  local result=$(curl -s1 -X DELETE -H "Content-Type: application/json" "$URL/cjobs" -d "id=$1")
  local status=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  if [[ "$status" == "OK" ]]
    then
      echo "Job deleted"
    else
      echo "ERROR when deleting job - status = "$status
  fi
  echo "-----------------"
}


function uploadAssignedLabels 
{
  #upload assigned labels
  echo "Uploading assignedLabels ... "
  
  local result=$(curl -s1 -X POST -H "Content-type:application/json" -H "Accept:text/plain" "$URL/cjobs/$1/assigns" -d '{"assigns":[
   {"worker":"worker1","object":"http://sunnyfun.com","label":{"value":4.399898705211159}},
   {"worker":"worker1","object":"http://sex-mission.com","label":{"value":-2.2209043312752503}},
   {"worker":"worker1","object":"http://google.com","label":{"value":0.2501928056080521}},
   {"worker":"worker1","object":"http://youporn.com","label":{"value":2.630393454314285}},
   {"worker":"worker1","object":"http://yahoo.com","label":{"value":1.7745486537318291}},
   {"worker":"worker2","object":"http://sunnyfun.com","label":{"value":2.0909755923562328}},
   {"worker":"worker2","object":"http://sex-mission.com","label":{"value":2.879992076430761}},
   {"worker":"worker2","object":"http://google.com","label":{"value":-0.800554365150114}},
   {"worker":"worker2","object":"http://youporn.com","label":{"value":-1.5672083199519249}},
   {"worker":"worker2","object":"http://yahoo.com","label":{"value":-1.2928498723416584}}
  ]}')
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  if [[ "$status" != "OK" ]]
      then
	echo "ERROR: Upload assigned label failed with status $status"
	#exit 1
      else
	echo "Uploaded successfully assigned labels $label"
  fi
}

function getJobStatus {
  local result=$(curl -s1 -X GET "$URL/$1")
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)

  if [[ "$status" != "$2" ]]
    then
      echo "$3 with status $status"
      exit 1
    else
      echo $4
  fi
  echo "-----------------"
}

function loadGoldLabels {
  #load gold labels
  echo "Loading the gold labels ..."
  local result=$(curl -s1 -X POST -H "Content-Type: application/json" "$URL/cjobs/$1/goldObjects" -d '{"objects":[
   { "object":"http://google.com","label":{"value":10.219077484951955,"zeta":0.292643407722905}},
   { "object":"http://sunnyfun.com","label":{"value":8.219077484951955,"zeta":0.343407722905}}
  ]}')
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  if [[ "$status" != "OK" ]]
    then
      echo "ERROR: Loading gold labels failed with status $status"
      #exit 1
    else
      echo "Loaded successfully the gold labels"
  fi
  echo "-----------------"
}

function loadUnassignedLabels {
  #load the unassigned labels
  echo "Loading the unassigned labels ..."
  local result=$(curl -s1 -X POST -H "Content-Type: application/json" "$URL/cjobs/$1/objects" -d '{"objects":["object1", "object2"]}')
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  if [[ "$status" != "OK" ]]
    then
      echo "ERROR: Loading unassigned labels failed with status $status"
      #exit 1
    else
      echo "Loaded successfully the unassigned labels"
  fi
  echo "-----------------"
}

function compute {
  #compute 
  echo "Starting to compute ..."
  local result=$(curl -s1 -X POST "$URL/cjobs/$1/compute")
  echo $result
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  echo $redirectId
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  if [[ "$status" != "OK" ]]
    then
      echo "ERROR: Data computation failed with status $status"
      #exit 1
    else
      echo "Data computation finished successfully"
  fi
  echo "-----------------"
}

function waitComputationToFinish {
  #get the job status and check that the data is correct
  echo "Checking if the computation has ended - redirect=$redirectId..."
  local result=$(curl -s1 -X GET "$URL/$redirectId")
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  while [[ $status != "Computation done" ]]
    do
      echo The status is $status - waiting 5 seconds
      sleep 5 
      result=$(curl -s1 -X GET "$URL/$redirectId")
      status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
    done
  echo $status
  echo "-----------------"
}


function getPredictionObjects {
  echo "Getting prediction objects"
  local result=$(curl -s1 -X GET "$URL/cjobs/$1/prediction/objects")
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  
  local result=$(curl -s1 -X GET "$URL/$redirectId")
  local status=$(echo $result| cut -d '[' -f 2 | cut -d ']' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  local predictionObjects=$(echo $result| cut -d '[' -f 2 | cut -d ']' -f 1)

  if [[ "$status" != "OK" ]]
    then
      echo "ERROR: Get prediction objects failed with status $status"
      #exit 1
    else
      echo "Got prediction objects"
  fi
  
  for i in "${expectedPredictionObjects[@]}"
  do
    if [[ "$predictionObjects" != *$i* ]]
      then
	echo "ERROR: Could not find object "$i" into the prediction objects data"
	#exit 1
    fi
  done
  
  echo "-----------------"
  
  
}

function getPredictionWorkers {
  echo "Getting prediction workers"
  local result=$(curl -s1 -X GET "$URL/cjobs/$1/prediction/workers")
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  
  local result=$(curl -s1 -X GET "$URL/$redirectId")
  echo $result
  
  if [[ "$result" != *"\"status\":\"OK\""* ]]
    then
      echo "ERROR: Get prediction workers failed"
      #exit 1
    else
      echo "Got prediction workers"
  fi
  
  echo "-----------------"
  
  
}


function galcTutorial
{
    JobID=$(createJob)
    uploadAssignedLabels $JobID
    loadGoldLabels $JobID
    loadUnassignedLabels $JobID
    compute $JobID
    waitComputationToFinish $JobID
   
    expectedPredictionObjects[0]='{"est_value":0.11456418171572877,"est_zeta":-0.1913547099132667,"distributionMu":0.22044451213182,"distributionSigma":0.5533196986062296,"object":{"name":"http://youporn.com"}}'
    expectedPredictionObjects[1]='{"est_value":0.41045876986866625,"est_zeta":0.343407722905,"distributionMu":0.22044451213182,"distributionSigma":0.5533196986062296,"object":{"name":"http://sunnyfun.com","goldLabel":{"value":8.219077484951955,"zeta":0.343407722905}}}'
    expectedPredictionObjects[2]='{"est_value":0.17414895890312143,"est_zeta":-0.08366872414864963,"distributionMu":0.22044451213182,"distributionSigma":0.5533196986062296,"object":{"name":"http://sex-mission.com"}}'
    expectedPredictionObjects[3]='{"est_value":0.38236987429215774,"est_zeta":0.292643407722905,"distributionMu":0.22044451213182,"distributionSigma":0.5533196986062296,"object":{"name":"http://google.com","goldLabel":{"value":10.219077484951955,"zeta":0.292643407722905}}}'
    expectedPredictionObjects[4]='{"est_value":NaN,"est_zeta":NaN,"distributionMu":0.22044451213182,"distributionSigma":0.5533196986062296,"object":{"name":"object1"}}'
    expectedPredictionObjects[5]='{"est_value":NaN,"est_zeta":NaN,"distributionMu":0.22044451213182,"distributionSigma":0.5533196986062296,"object":{"name":"object2"}}'
    expectedPredictionObjects[6]='{"est_value":0.05636583076319032,"est_zeta":-0.2965350443548846,"distributionMu":0.22044451213182,"distributionSigma":0.5533196986062296,"object":{"name":"http://yahoo.com"}}'

    getPredictionObjects $JobID $expectedPredictionObjects
    
    getPredictionWorkers $JobID 
    deleteJob $JobID
    
}

galcTutorial