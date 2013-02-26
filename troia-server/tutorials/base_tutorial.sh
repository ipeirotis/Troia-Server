#!/bin/bash

#URL="http://localhost:8080/troia-server-1.0"
URL="http://project-troia.com/api"
redirectId=0
noIterations=20
algorithm=DS
labelChoosingMethod=MaxLikelihood

function createJob 
{
  local result=$(curl -s1 -X POST -H "Content-Type: application/json" "$URL/jobs" -d "type=batch&categories=[
    {"prior":"0.5", "name":"porn", "misclassificationCost": [{'categoryName': 'porn', 'value': 0}, {'categoryName': 'notporn', 'value': 1}]}, 
    {"prior":"0.5", "name":"notporn", "misclassificationCost":[{'categoryName': 'porn', 'value': 1}, {'categoryName': 'notporn', 'value': 0}]}]")
 
  local status=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  while [[ $status != "OK" ]]
    do
      sleep 5 
      result=$(curl -s1 -X POST -H "Content-Type: application/json" "$URL/jobs" -d "type=batch&categories=[
      {"prior":"0.5","name":"porn","misclassification_cost":{"porn":"0","notporn":"1"}},
      {"prior":"0.5","name":"notporn","misclassification_cost":{"porn":"1","notporn":"0"}}]")
      status=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
    done
  local jid=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 3 | cut -d '"' -f 1)
  echo "$jid"
}


function deleteJob 
{
  echo "Deleting job with ID $1 ..."
  local result=$(curl -s1 -X DELETE -H "Content-Type: application/json" "$URL/jobs" -d "id=$1")
  echo $result
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
  local result=$(curl -s1 -X POST -H "Content-Type: application/json" "$URL/jobs/$1/assignedLabels" -d 'labels=[
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
  echo $result
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  if [[ "$status" != "OK" ]]
    then
      echo "ERROR: Upload assigned labels failed with status $status"
      #exit 1
    else
      echo "Uploaded successfully the assigned labels"
  fi
  echo $result
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
}

function loadGoldLabels {
  #load gold labels
  echo "Loading the gold labels ..."
  local result=$(curl -s1 -X POST -H "Content-Type: application/json" "$URL/jobs/$1/goldData" -d 'labels=
  [{
    "correctCategory": "notporn",
    "objectName": "http://google.com"
  }]')
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

function compute {
  #compute 
  echo "Computing - using $noIterations iterations ..."
  local result=$(curl -s1 -X POST -d "iterations=$noIterations" "$URL/jobs/$1/compute")
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
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

function getWorkersScore {
  echo "Getting the workers score ... "
  local result=$(curl -s1 -X GET "$URL/jobs/$1/prediction/workersScore")
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  if [[ "$status" != "OK" ]]
    then
      echo "ERROR: Get workers score job failed with status $status"
      #exit 1
    else
      echo "Get workers score job finished successfully"
  fi
  echo "-----------------"
}

function getWorkersQuality {
  #get the workers quality
  echo "Getting the workers quality ... "
  local result=$(curl -s1 -X GET "$URL/jobs/$1/prediction/workersQuality")
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  if [[ "$status" != "OK" ]]
    then
      echo "ERROR: Get workers quality job failed with status $status"
      #exit 1
    else
      echo "Get workers quality job finished successfully"
  fi
}

function getWorkersQualityData {
  #get the job status and check that the data is correct
  echo "Getting workers quality job status for redirect=$redirectId..."
  local result=$(curl -s1 -X GET "$URL/$redirectId")
  local status=$(echo $result| cut -d '[' -f 2 | cut -d ']' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  local workerQualityData=$(echo $result| cut -d '[' -f 2 | cut -d ']' -f 1)
  echo "Received: $workerQualityData"

  if [[ "$status" != "OK" ]]
    then
      echo "ERROR: Get worker quality job status failed with status $status"
      #exit 1
    else
      echo "Got workers quality job status"
  fi
  
  for i in "${!expectedWorkerQualities[@]}"
  do
    if [[ "$workerQualityData" != *\"workerName\":"\"$i\",\"value\":${expectedWorkerQualities[$i]}"* ]]
      then
	echo "ERROR: Could not find object "\"workerName\":\"$i\",\"value\":${expectedWorkerQualities[$i]}" into the worker quality data"
	#exit 1
    fi
  done
  echo "-----------------"
}

function getPredictionData {
  echo "Getting prediction data ..."
  local result=$(curl -s1 -X GET "$URL/jobs/$1/prediction/data?algorithm=$algorithm&labelChoosing=$labelChoosingMethod")
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  if [[ "$status" != "OK" ]]
    then
      echo "ERROR: Get prediction data job failed with status $status"
      #exit 1
    else
      echo "Get prediction data job finished successfully"
  fi
}

function getActualPredictionData {
  #get the job status and check that the returned data is correct
  echo "Getting prediction data job status for redirect=$redirectId ..."
  local result=$(curl -s1 -X GET "$URL/$redirectId")
  local status=$(echo $result| cut -d '[' -f 2 | cut -d ']' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  local predictionData=$(echo $result| cut -d '[' -f 2 | cut -d ']' -f 1)
  echo "Received: $predictionData"

  if [[ "$status" != "OK" ]]
    then
      echo "ERROR: Get prediction data job status failed with status $status"
      #exit 1
    else
      echo "Got prediction data job status"
  fi
  
  for i in "${!expectedCategories[@]}"
  do
    if [[ "$predictionData" != *\"objectName\":"\"$i\",\"categoryName\":\"${expectedCategories[$i]}\""* ]]
      then
	echo "ERROR: Could not find object "\"objectName\":\"$i\",\"categoryName\":\"${expectedCategories[$i]}\"" into the prediction data"
	#exit 1
    fi
  done

  echo "-----------------"
}

function getLabelProbabilityDistribution {
  echo "Getting label probability distributions for object $2 ..."
  local result=$(curl -s1 -X GET "$URL/jobs/$1/data/$2/categoryProbability")
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  
  local result=$(curl -s1 -X GET "$URL/$redirectId")
  local status=$(echo $result| cut -d '[' -f 2 | cut -d ']' -f 2| cut -d ':' -f 2 | cut -d '"' -f 2)
  while [[ $status != "OK" ]]
    do
      echo The status is $status - waiting 5 seconds
      sleep 5 
      result=$(curl -s1 -X GET "$URL/$redirectId")
      status=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
      echo $status
    done
  local probDistributionData=$(echo $result| cut -d '[' -f 2 | cut -d ']' -f 1)
  echo "Received: $probDistributionData"
  
  for i in "${!expectedCategoryDistributions[@]}"
  do
    if [[ "$probDistributionData" != *\"categoryName\":"\"$i\",\"value\":${expectedCategoryDistributions[$i]}"* ]]
      then
	echo "ERROR: Could not find object "\"categoryName\":\"$i\",\"value\":${expectedCategoryDistributions[$i]}" into the worker quality data"
	#exit 1
    fi
  done
  
  echo "-----------------"
}

function mainFlow {
  JobID=$(createJob)
  uploadAssignedLabels $JobID
  getJobStatus $redirectId "Assigns added" "Get assigned labels failed" "Got successfully job status for assigned labels"
  loadGoldLabels $JobID
  compute $JobID
  waitComputationToFinish
  getWorkersScore $JobID

  getWorkersQuality $JobID
  declare -A expectedWorkerQualities
  expectedWorkerQualities[worker1]=0.0
  expectedWorkerQualities[worker2]=0.4444444444444444
  expectedWorkerQualities[worker3]=1.0
  expectedWorkerQualities[worker4]=1.0
  expectedWorkerQualities[worker5]=1.0
  getWorkersQualityData $expectedWorkerQualities

  getPredictionData $JobID
  declare -A expectedCategories
  expectedCategories[http://google.com]=notporn
  expectedCategories[http://sex-mission.com]=porn
  expectedCategories[http://sunnyfun.com]=notporn
  expectedCategories[http://yahoo.com]=notporn
  expectedCategories[http://youporn.com]=porn
  getActualPredictionData $expectedCategories
  
  datumObject=http://sunnyfun.com
  declare -A expectedCategoryDistributions
  expectedCategoryDistributions[porn]=0.0
  expectedCategoryDistributions[notporn]=1.0
  getLabelProbabilityDistribution $JobID $datumObject
  
  deleteJob $JobID
}

mainFlow

