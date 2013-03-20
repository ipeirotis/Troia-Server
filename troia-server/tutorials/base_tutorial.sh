#!/bin/bash

URL="http://localhost:8080/troia-server-1.0"
#URL="http://project-troia.com/api"
redirectId=0
noIterations=20
costAlgorithm=ExpectedCost
labelChoosingMethod=MaxLikelihood

function createJob 
{
  local result=$(curl -s1 -X POST -H "Content-Type: application/json" "$URL/jobs" -d "{categories:[
    {"prior":"0.5", "name":"porn", "misclassificationCost": [{'categoryName': 'porn', 'value': 0}, {'categoryName': 'notporn', 'value': 1}]}, 
    {"prior":"0.5", "name":"notporn", "misclassificationCost":[{'categoryName': 'porn', 'value': 1}, {'categoryName': 'notporn', 'value': 0}]}], algorithm:'BDS'}")

  local status=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  while [[ $status != "OK" ]]
    do
      sleep 5 
      result=$(curl -s1 -X POST -H "Content-Type: application/json" "$URL/jobs" -d "{categories:[
      {"prior":"0.5","name":"porn","misclassification_cost":{"porn":"0","notporn":"1"}},
      {"prior":"0.5","name":"notporn","misclassification_cost":{"porn":"1","notporn":"0"}}], algorithm: 'BDS'}")
      status=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
    done
  local jid=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 3 | cut -d '"' -f 1)
  echo "$jid"
}


function deleteJob 
{
  echo "Deleting job with ID $1 ..."
  local result=$(curl -s1 -X DELETE -H "Content-Type: application/json" "$URL/jobs" -d "id=$1")
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
  local result=$(curl -s1 -X POST -H "Content-Type: application/json" "$URL/jobs/$1/assigns" -d '{assigns:[
    {"worker":"worker1","object":"http://sunnyfun.com","label":"porn"},
    {"worker":"worker1","object":"http://sex-mission.com","label":"porn"},
    {"worker":"worker1","object":"http://google.com","label":"porn"},
    {"worker":"worker1","object":"http://youporn.com","label":"porn"},
    {"worker":"worker1","object":"http://yahoo.com","label":"porn"},
    {"worker":"worker2","object":"http://sunnyfun.com","label":"notporn"},
    {"worker":"worker2","object":"http://sex-mission.com","label":"porn"},
    {"worker":"worker2","object":"http://google.com","label":"notporn"},
    {"worker":"worker2","object":"http://youporn.com","label":"porn"},
    {"worker":"worker2","object":"http://yahoo.com","label":"porn"},
    {"worker":"worker3","object":"http://sunnyfun.com","label":"notporn"},
    {"worker":"worker3","object":"http://sex-mission.com","label":"porn"},
    {"worker":"worker3","object":"http://google.com","label":"notporn"},
    {"worker":"worker3","object":"http://youporn.com","label":"porn"},
    {"worker":"worker3","object":"http://yahoo.com","label":"notporn"},
    {"worker":"worker4","object":"http://sunnyfun.com","label":"notporn"},
    {"worker":"worker4","object":"http://sex-mission.com","label":"porn"},
    {"worker":"worker4","object":"http://google.com","label":"notporn"},
    {"worker":"worker4","object":"http://youporn.com","label":"porn"},
    {"worker":"worker4","object":"http://yahoo.com","label":"notporn"},
    {"worker":"worker5","object":"http://sunnyfun.com","label":"porn"},
    {"worker":"worker5","object":"http://sex-mission.com","label":"notporn"},
    {"worker":"worker5","object":"http://google.com","label":"porn"},
    {"worker":"worker5","object":"http://youporn.com","label":"notporn"},
    {"worker":"worker5","object":"http://yahoo.com","label":"porn"}]}')
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  if [[ "$status" != "OK" ]]
    then
      echo "ERROR: Upload assigned labels failed with status $status"
      #exit 1
    else
      echo "Uploaded successfully the assigned labels"
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
}

function loadGoldLabels {
  #load gold labels
  echo "Loading the gold labels ..."
  local result=$(curl -s1 -X POST -H "Content-Type: application/json" "$URL/jobs/$1/goldObjects" -d '{objects:
  [{
    "goldLabel": "notporn",
    "name": "http://google.com"
  }]}')
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


function getEstimatedWorkersQuality {
  #get the estimated workers quality
  echo "Getting the estimated workers quality ... "
  local result=$(curl -s1 -X GET "$URL/jobs/$1/workers/quality/estimated" -d "{costAlgorithm: $costAlgorithm}")
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  if [[ "$status" != "OK" ]]
    then
      echo "ERROR: Get estimated workers quality job failed with status $status"
      #exit 1
    else
      echo "Get estimated workers quality job finished successfully"
  fi
}

function getEstimatedWorkersQualityData {
  #get the job status and check that the data is correct
  echo "Getting estimated workers quality job status for redirect=$redirectId..."
  local result=$(curl -s1 -X GET "$URL/$redirectId")
  local status=$(echo $result| cut -d '[' -f 2 | cut -d ']' -f 2 | cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
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

function getPredictedObjectsCategories {
  echo "Getting predicted object categories ..."
  local result=$(curl -s1 -X GET "$URL/jobs/$1/objects/prediction")
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

function getActualPredictedObjectsCategories {
  #get the job status and check that the returned data is correct
  echo "Getting prediction data job status for redirect=$redirectId ..."
  local result=$(curl -s1 -X GET "$URL/$redirectId")
  local status=$(echo $result| cut -d '[' -f 2 | cut -d ']' -f 2 | cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
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
  local result=$(curl -s1 -X GET "$URL/jobs/$1/objects/$2/categoryProbability")
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  
  local result=$(curl -s1 -X GET "$URL/$redirectId")
  local status=$(echo $result| cut -d '[' -f 2 | cut -d ']' -f 2| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
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

  getEstimatedWorkersQuality $JobID
  declare -A expectedWorkerQualities
  expectedWorkerQualities[worker1]=0.0
  expectedWorkerQualities[worker2]=0.4444444444444444
  expectedWorkerQualities[worker3]=1.0
  expectedWorkerQualities[worker4]=1.0
  expectedWorkerQualities[worker5]=1.0
  getEstimatedWorkersQualityData $expectedWorkerQualities

  getPredictedObjectsCategories $JobID
  declare -A expectedCategories
  expectedCategories[http://google.com]=notporn
  expectedCategories[http://sex-mission.com]=porn
  expectedCategories[http://sunnyfun.com]=notporn
  expectedCategories[http://yahoo.com]=notporn
  expectedCategories[http://youporn.com]=porn
  getActualPredictedObjectsCategories $expectedCategories
  
  datumObject=http://sunnyfun.com
  declare -A expectedCategoryDistributions
  expectedCategoryDistributions[porn]=0.0
  expectedCategoryDistributions[notporn]=1.0
  getLabelProbabilityDistribution $JobID $datumObject
  
  deleteJob $JobID
}

mainFlow

