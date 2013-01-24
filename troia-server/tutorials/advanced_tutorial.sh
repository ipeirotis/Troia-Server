#!/bin/bash 

URL="http://localhost:8080/troia-server-0.8"
#URL="http://project-troia.com/api"
JobID=""
redirectId=0
noIterations=20

function generateRandomJobId {
  rJobId=$(tr -dc "[:alpha:]" < /dev/urandom | head -c $1)
  echo "$rJobId"
}

function createJob 
{
  echo "Creating a new job ..."
  local result=$(curl -s1 -X POST -H "Content-Type: application/json" "$URL/jobs" -d "id=$JobID&type=incremental&categories=[
    {"prior":"0.5","name":"porn","misclassification_cost":{"porn":"0","notporn":"1"}},
    {"prior":"0.5","name":"notporn","misclassification_cost":{"porn":"1","notporn":"0"}}]")
  local status=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  echo $JobID
  
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

function getPredictionData {
  #get the prediction data
  echo "Getting the prediction data ..."
  local result=$(curl -s1 -X GET "$URL/jobs/$JobID/prediction/data")
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)

  if [[ "$status" != "OK" ]]
    then
      echo "Get prediction data failed with status $status"
      exit 1
    else
      echo "Got successfully the prediction data"
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
	echo "ERROR: Could not find object "\"$i\":\"${expectedCategories[$i]}\"" into the prediction data"
	#exit 1
    fi
  done

  echo "-----------------"
}


function getProbabilityDistribution {

  #get the category probability distribution
  echo "Getting the category probability distribution data ..."
  local result=$(curl -s1 -X GET "$URL/jobs/$JobID/data/http://yahoo.com/categoryProbability")
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)

  if [[ "$status" != "OK" ]]
    then
      echo "Get category probabilty failed with status $status"
      exit 1
    else
      echo "Got successfully category probability distribution data"
  fi
}


function getCategoryProbabilities {
  #get the job status and check that the data is correct
  echo "Getting category probability job status for redirect=$redirectId..."
  local result=$(curl -s1 -X GET "$URL/jobs/$JobID/status/$redirectId")
  local status=$(echo $result| cut -d ',' -f 4 | cut -d ':' -f 2 | cut -d '"' -f 2)
  local categProbabilityData=$(echo $result| cut -d '{' -f 3 | cut -d '}' -f 1)
  echo "Received:  $categProbabilityData"

  if [[ "$status" != "OK" ]]
    then
      echo "Get category probability job status failed with status $status"
      exit 1
    else
      echo "Got category probability job status"
  fi
  
  for i in "${!expectedProbabilities[@]}"
  do
    if [[ "$categProbabilityData" != *"\"$i\":${expectedProbabilities[$i]}"* ]]
      then
	echo "ERROR: Could not find object "\"$i\":${expectedProbabilities[$i]}" into the category probability data"
	#exit 1
    fi
  done
  echo "-----------------"
}


function getPredictedWorkersQuality {
  #get the workers quality
  echo "Getting predicted workers quality ..."
  local result=$(curl -s1 -X GET "$URL/jobs/$JobID/prediction/workersQuality")
  local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)

  if [[ "$status" != "OK" ]]
    then
      echo "Get predicted worker quality failed with status $status"
      exit 1
    else
      echo "Got successfully predicted worker quality"
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
	echo "ERROR: Could not find object "\"$i\":${expectedWorkerQualities[$i]}" into the worker quality data"
	#exit 1
    fi
  done
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

function loadGoldLabels {
  #load gold labels
  echo "Loading the gold labels ..."
  local result=$(curl -s1 -X POST -H "Content-Type: application/json" "$URL/jobs/$JobID/goldData" -d 'labels=
  [{
    "correctCategory": "porn",
    "objectName": "http://sex-mission.com"
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

function getDataCost {
  echo "Getting data cost for costAlgorithm=$1 ..."
  local result=$(curl -s1 -X GET "$URL/jobs/$JobID/prediction/dataCost" -d "costAlgorithm=$1")
  redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  
  echo "Getting the actual data cost for redirect=$redirectId..."
  local result=$(curl -s1 -X GET "$URL/jobs/$JobID/status/$redirectId")
  local status=$(echo $result| cut -d '{' -f 3 | cut -d '}' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
  local dataCost=$(echo $result| cut -d '{' -f 3 | cut -d '}' -f 1)
  echo "Received: $dataCost"

  if [[ "$status" != "OK" ]]
    then
      echo "Get data cost job status failed with status $status"
      exit 1
    else
      echo "Got data cost job status"
  fi
  
  for i in "${!expectedDataCost[@]}"
  do
    if [[ "$dataCost" != *"\"$i\":${expectedDataCost[$i]}"* ]]
      then
	echo "ERROR: Could not find object "\"$i\":${expectedDataCost[$i]}" into the dataCost data"
	#exit 1
    fi
  done
  echo "-----------------"
}

function advancedTutorialPart1 {
  JobID=$(generateRandomJobId 10)
  createJob
  uploadAssignedLabels
  getJobStatus $redirectId "Assigns added" "Get assigned labels failed" "Got successfully job status for assigned labels"

  getPredictionData
  declare -A expectedCategories
  expectedCategories[http://google.com]=notporn
  expectedCategories[http://sex-mission.com]=porn
  expectedCategories[http://sunnyfun.com]=notporn
  expectedCategories[http://yahoo.com]=notporn
  expectedCategories[http://youporn.com]=porn
  getActualPredictionData $expectedCategories

  getProbabilityDistribution
  declare -A expectedProbabilities 
  expectedProbabilities[notporn]=0.97945
  expectedProbabilities[porn]=0.02055
  getCategoryProbabilities $expectedProbabilities

  getPredictedWorkersQuality
  declare -A expectedWorkerQualities
  expectedWorkerQualities[worker1]=0.8516375211366187
  expectedWorkerQualities[worker2]=0.9011184676007533
  expectedWorkerQualities[worker3]=0.9749015350602155
  expectedWorkerQualities[worker4]=0.9749015350602155
  expectedWorkerQualities[worker5]=0.8439714531050402
  getWorkersQualityData $expectedWorkerQualities

  compute
  
  getProbabilityDistribution
  declare -A expectedProbabilities 
  expectedProbabilities[notporn]=0.97998
  expectedProbabilities[porn]=0.02002
  getCategoryProbabilities $expectedProbabilities
  
 
}

function advancedTutorialPart2 {
  JobID=$(generateRandomJobId 10)
  createJob
  uploadAssignedLabels
  getJobStatus $redirectId "Assigns added" "Get assigned labels failed" "Got successfully job status for assigned labels"

  getPredictionData
  declare -A expectedCategories
  expectedCategories[http://google.com]=notporn
  expectedCategories[http://sex-mission.com]=porn
  expectedCategories[http://sunnyfun.com]=notporn
  expectedCategories[http://yahoo.com]=notporn
  expectedCategories[http://youporn.com]=porn
  getActualPredictionData $expectedCategories

  getProbabilityDistribution
  declare -A expectedProbabilities 
  expectedProbabilities[notporn]=0.97945
  expectedProbabilities[porn]=0.02055
  getCategoryProbabilities $expectedProbabilities

  getPredictedWorkersQuality
  declare -A expectedWorkerQualities
  expectedWorkerQualities[worker1]=0.8516375211366187
  expectedWorkerQualities[worker2]=0.9011184676007533
  expectedWorkerQualities[worker3]=0.9749015350602155
  expectedWorkerQualities[worker4]=0.9749015350602155
  expectedWorkerQualities[worker5]=0.8439714531050402
  getWorkersQualityData $expectedWorkerQualities
  
  loadGoldLabels
  getPredictedWorkersQuality
  declare -A expectedWorkerQualities
  expectedWorkerQualities[worker1]=0.8516375211366187
  expectedWorkerQualities[worker2]=0.9011184676007533
  expectedWorkerQualities[worker3]=0.9749015350602155
  expectedWorkerQualities[worker4]=0.9749015350602155
  expectedWorkerQualities[worker5]=0.8439714531050402
  getWorkersQualityData $expectedWorkerQualities
}

function advancedTutorialPart3 {
  JobID=$(generateRandomJobId 10)
  createJob
  uploadAssignedLabels
  getJobStatus $redirectId "Assigns added" "Get assigned labels failed" "Got successfully job status for assigned labels"
  
  compute

  declare -A expectedDataCost
  expectedDataCost[http://google.com]=0.0032148158
  expectedDataCost[http://sex-mission.com]=0.0015388141999999997
  expectedDataCost[http://sunnyfun.com]=0.00029995499999999997
  expectedDataCost[http://yahoo.com]=0.00017998380000000002
  expectedDataCost[http://youporn.com]=0.040255395
  getDataCost ExpectedCost expectedDataCost
}


echo "###Executing the first part of the tutorial###"
echo "##############################################"
advancedTutorialPart1
echo "##############################################"

echo "###Executing the second part of the tutorial###"
echo "##############################################"
advancedTutorialPart2
echo "##############################################"

echo "###Executing the third part of the tutorial###"
echo "##############################################"
advancedTutorialPart3
echo "##############################################"
