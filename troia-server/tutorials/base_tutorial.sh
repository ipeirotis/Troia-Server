#!/bin/bash

#URL="http://localhost:8080/troia-server-0.8"
URL="http://project-troia.com/api"
JobID="test_3"
redirectId=0
noIterations=20
algorithm=DS
labelChoosingMethod=MaxLikelihood

function createJob 
{
  echo "Creating a new job ..."
  local result=$(curl -s1 -X POST -H "Content-Type: application/json" "$URL/jobs" -d "id=$JobID&type=incremental&categories=[
    {"prior":"1","name":"porn","misclassification_cost":{"porn":"0","notporn":"1"}},
    {"prior":"1","name":"notporn","misclassification_cost":{"porn":"1","notporn":"0"}}]")
  local status=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  if [[ "$status" != "OK" ]]
    then
      echo "Job with id $JobID already exists"
    else
      echo "Created new job with id $JobID"
  fi
  echo "-----------------"
}

function uploadAssignedLabels 
{
  #upload assigned labels
  echo "Uploading assignedLabels ..."
  local result=$(curl -s1 -X POST -H "Content-Type: application/json" "$URL/jobs/$JobID/assignedLabels" -d 'labels=[
    {"workerName":"worker1","objectName":"http://sunnyfun.com","categoryName":"porn"},
    {"workerName":"worker1","objectName":"http://sex-mission.com","categoryName":"porn"},
    {"workerName":"worker1","objectName":"http://google.com","categoryName":"porn"},
    {"workerName":"worker1","objectName":"http://youporn.com","categoryName":"porn"},
    {"workerName":"worker1","objectName":"http://yahoo.com","categoryName":"porn"},
    {"workerName":"worker2","objectName":"http://sunnyfun.com","categoryName":"notporn"},
    {"workerName":"worker2","objectName":"http://sex-mission.com","categoryName":"porn"},
    {"workerName":"worker2","objectName":"http://google.com","categoryName":"notporn"},
    {"workerName":"worker2","objectName":"http://youporn.com","categoryName":"porn"},
    {"workerName":"worker2","objectName":"http://yahoo.com","categoryName":"porn"},
    {"workerName":"worker3","objectName":"http://sunnyfun.com","categoryName":"notporn"},
    {"workerName":"worker3","objectName":"http://sex-mission.com","categoryName":"porn"},
    {"workerName":"worker3","objectName":"http://google.com","categoryName":"notporn"},
    {"workerName":"worker3","objectName":"http://youporn.com","categoryName":"porn"},
    {"workerName":"worker3","objectName":"http://yahoo.com","categoryName":"notporn"},
    {"workerName":"worker4","objectName":"http://sunnyfun.com","categoryName":"notporn"},
    {"workerName":"worker4","objectName":"http://sex-mission.com","categoryName":"porn"},
    {"workerName":"worker4","objectName":"http://google.com","categoryName":"notporn"},
    {"workerName":"worker4","objectName":"http://youporn.com","categoryName":"porn"},
    {"workerName":"worker4","objectName":"http://yahoo.com","categoryName":"notporn"},
    {"workerName":"worker5","objectName":"http://sunnyfun.com","categoryName":"porn"},
    {"workerName":"worker5","objectName":"http://sex-mission.com","categoryName":"notporn"},
    {"workerName":"worker5","objectName":"http://google.com","categoryName":"porn"},
    {"workerName":"worker5","objectName":"http://youporn.com","categoryName":"notporn"},
    {"workerName":"worker5","objectName":"http://yahoo.com","categoryName":"porn"}]')

  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  if [[ "$status" != "OK" ]]
    then
      echo "Upload assigned labels failed with status $status"
      exit 1
    else
      echo "Uploaded successfully the assigned labels"
  fi
}

function getJobStatus {
  local result=$(curl -s1 -X GET "$URL/jobs/$JobID/status/$1")
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)

  if [[ "$status" != "$2" ]]
    then
      echo "$3 with status $status"
      exit 1
    else
      echo $4
  fi
}

function loadGoldLabels {
  #load gold labels
  echo "Loading the gold labels ..."
  local result=$(curl -s1 -X POST -H "Content-Type: application/json" "$URL/jobs/$JobID/goldData" -d 'labels=
  [{
    "correctCategory": "notporn",
    "objectName": "http://google.com"
  }]')
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  if [[ "$status" != "OK" ]]
    then
      echo "Loading gold labels failed with status $status"
      exit 1
    else
      echo "Loaded successfully the gold labels"
  fi
  echo "-----------------"
}

function compute {
  #compute 
  echo "Computing - using $noIterations iterations ..."
  local result=$(curl -s1 -X POST -d "iterations=$noIterations" "$URL/jobs/$JobID/compute")
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  if [[ "$status" != "OK" ]]
    then
      echo "Data computation failed with status $status"
      exit 1
    else
      echo "Data computation finished successfully"
  fi
  echo "-----------------"
}

function waitComputationToFinish {
  #get the job status and check that the data is correct
  echo "Checking if the computation has ended - redirect=$redirectId..."
  local result=$(curl -s1 -X GET "$URL/jobs/$JobID/status/$redirectId")
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  while [[ $status != "Computation done" ]]
    do
      echo The status is $status - waiting 5 seconds
      sleep 5 
      result=$(curl -s1 -X GET "$URL/jobs/$JobID/status/$redirectId")
      status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
    done
  echo $status
  echo "-----------------"
}

function getWorkersScore {
  echo "Getting the workers score ... "
  local result=$(curl -s1 -X GET "$URL/jobs/$JobID/prediction/workersScore")
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  if [[ "$status" != "OK" ]]
    then
      echo "Get workers score job failed with status $status"
      exit 1
    else
      echo "Get workers score job finished successfully"
  fi
  echo "-----------------"
}

function getWorkersQuality {
  #get the workers quality
  echo "Getting the workers quality ... "
  local result=$(curl -s1 -X GET "$URL/jobs/$JobID/prediction/workersQuality")
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  if [[ "$status" != "OK" ]]
    then
      echo "Get workers quality job failed with status $status"
      exit 1
    else
      echo "Get workers quality job finished successfully"
  fi
}

function getWorkersQualityData {
  #get the job status and check that the data is correct
  echo "Getting workers quality job status for redirect=$redirectId..."
  local result=$(curl -s1 -X GET "$URL/jobs/$JobID/status/$redirectId")
  local status=$(echo $result| cut -d '{' -f 3 | cut -d '}' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  local workerQualityData=$(echo $result| cut -d '{' -f 3 | cut -d '}' -f 1)
  echo "Received: $workerQualityData"

  if [[ "$status" != "OK" ]]
    then
      echo "Get worker quality job status failed with status $status"
      exit 1
    else
      echo "Got workers quality job status"
  fi
  
  for i in "${!expectedWorkerQualities[@]}"
  do
    if [[ "$workerQualityData" != *"\"$i\":${expectedWorkerQualities[$i]}"* ]]
      then
	echo "Didn't find object "\"$i\":${expectedWorkerQualities[$i]}" into the worker quality data"
	#exit 1
    fi
  done
  echo "-----------------"
}

function getPredictionData {
  echo "Getting prediction data ..."
  local result=$(curl -s1 -X GET "$URL/jobs/$JobID/prediction/data?algorithm=$algorithm&labelChoosing=$labelChoosingMethod")
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  if [[ "$status" != "OK" ]]
    then
      echo "Get workers quality job failed with status $status"
      exit 1
    else
      echo "Get workers quality job finished successfully"
  fi
}

function getActualPredictionData {
  #get the job status and check that the returned data is correct
  echo "Getting prediction data job status for redirect=$redirectId ..."
  local result=$(curl -s1 -X GET "$URL/jobs/$JobID/status/$redirectId")
  local status=$(echo $result| cut -d '{' -f 3 | cut -d '}' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  local predictionData=$(echo $result| cut -d '{' -f 3 | cut -d '}' -f 1)
  echo "Received: $predictionData"

  if [[ "$status" != "OK" ]]
    then
      echo "Get prediction data job status failed with status $status"
      exit 1
    else
      echo "Got prediction data job status"
  fi
  
  for i in "${!expectedCategories[@]}"
  do
    if [[ "$predictionData" != *"\"$i\":\"${expectedCategories[$i]}\""* ]]
      then
	echo "Didn't find object "\"$i\":\"${expectedCategories[$i]}\"" into the prediction data"
	#exit 1
    fi
  done

  echo "-----------------"
}

createJob
uploadAssignedLabels
getJobStatus $redirectId "Assigns added" "Get assigned labels failed" "Got successfully job status for assigned labels"
loadGoldLabels
compute
waitComputationToFinish
getWorkersScore

getWorkersQuality
declare -A expectedWorkerQualities
expectedWorkerQualities[worker1]=-1.0
expectedWorkerQualities[worker2]=-0.11111111111111116
expectedWorkerQualities[worker3]=1.0
expectedWorkerQualities[worker4]=1.0
expectedWorkerQualities[worker5]=1.0
getWorkersQualityData $expectedWorkerQualities

getPredictionData
declare -A expectedCategories
expectedCategories[http://google.com]=notporn
expectedCategories[http://sex-mission.com]=porn
expectedCategories[http://sunnyfun.com]=notporn
expectedCategories[http://yahoo.com]=notporn
expectedCategories[http://youporn.com]=porn
getActualPredictionData $expectedCategories

