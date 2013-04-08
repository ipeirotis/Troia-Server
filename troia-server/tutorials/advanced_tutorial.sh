#!/bin/bash 

source ./base_tutorial_calls.sh

redirectId=0
noIterations=20

function createJob
{
    echo $(curl -s1 -X POST -H "Content-Type: application/json" "$URL/jobs" -d '{
        categories: ["porn", "notporn"],
        categoryPriors: [
            {"categoryName": "porn", "value": 0.5},
            {"categoryName": "notporn", "value": 0.5}],
        costMatrix: [
            {"from": "porn", "to": "notporn", "value": 1.0},
            {"from": "porn", "to": "porn", "value": 0.0},
            {"from": "notporn", "to": "porn", "value": 1.0},
            {"from": "notporn", "to": "notporn", "value": 0.0}],
        algorithm: "IDS"
    }')
}

function loadGoldLabels
{
    local jid=$1
    echo $(curl -s1 -X POST -H "Content-Type: application/json" "$URL/jobs/$jid/goldObjects" -d '{
        objects:
            [{
                "correctCategory": "porn",
                "objectName": "http://sex-mission.com"
            }]
        }'
    )
}

function getObjectCategoryProbability
{
    local jid=$1
    echo $(curl -s1 -X GET "$URL/jobs/$jid/objects/http://yahoo.com/categoryProbability")
}

loadAssignedLabelsExpected='
{
    "executionTime": 0.002, 
    "result": "Assigns added", 
    "status": "OK", 
    "timestamp": "2013-04-08T17:33:54.660+02:00"
}'

loadGoldLabelsExpected='
{
    "executionTime": 0.0, 
    "result": "Objects added", 
    "status": "OK", 
    "timestamp": "2013-04-09T01:12:19.175+02:00"
}'


getObjectCategoryProbabilityExpected='
{
    "executionTime": 0.0, 
    "result": [
        {
            "categoryName": "porn", 
            "value": 0.19341
        }, 
        {
            "categoryName": "notporn", 
            "value": 0.80659
        }
    ], 
    "status": "OK", 
    "timestamp": "2013-04-08T17:01:44.782+02:00"
}'

getObjectsCategoriesExpected='
{
    "result": [
        {"objectName":"http://google.com","categoryName":"notporn"},
        {"objectName":"http://youporn.com","categoryName":"porn"},
        {"objectName":"http://sunnyfun.com","categoryName":"notporn"},
        {"objectName":"http://sex-mission.com","categoryName":"porn"},
        {"objectName":"http://yahoo.com","categoryName":"notporn"}
    ],
    "status": "OK",
    "timestamp": "2013-01-15T15:53:30.378+01:00"
}'

getWorkersQualitiesExpected='
{
    "executionTime": 0.0, 
    "result": [
        {
            "value": 0.12063846389805155, 
            "workerName": "worker2"
        }, 
        {
            "value": 0.2876931124396629, 
            "workerName": "worker3"
        }, 
        {
            "value": 0.48330514536747027, 
            "workerName": "worker4"
        }, 
        {
            "value": 0.13734139997114614, 
            "workerName": "worker5"
        }, 
        {
            "value": 0.10767027995295109, 
            "workerName": "worker1"
        }
    ], 
    "status": "OK", 
    "timestamp": "2013-04-09T00:35:20.100+02:00"
}'

function advancedTutorialPart1
{
     # Create a job.
    response=$(createJob)
    assertStatus "$response"

    # Extract job id from the response.
    jid=$(echo $response | cut -d ',' -f 2 | cut -d ':' -f 3 | cut -d '"' -f 1 | tr -d ' ')

    testAsyncJobCallResponse "loadAssignedLabels"           "$jid" "$loadAssignedLabelsExpected"
    testAsyncJobCallResponse "getObjectCategoryProbability" "$jid" "$getObjectCategoryProbabilityExpected"
    testAsyncJobCallResponse "getObjectsCategories"         "$jid" "$getObjectsCategoriesExpected"
    testAsyncJobCallResponse "getWorkersQualities"          "$jid" "$getWorkersQualitiesExpected"
    testAsyncJobCallResponse "loadGoldLabels"               "$jid" "$loadGoldLabelsExpected"
    exit

    deleteJob $JobID
}

function advancedTutorialPart2 {
  JobID=$(createJob)
  uploadAssignedLabels $JobID
  getJobStatus $redirectId "Assigns added" "Get assigned labels failed" "Got successfully job status for assigned labels"

  getPredictionData $JobID
  declare -A expectedCategories
  expectedCategories[http://google.com]=notporn
  expectedCategories[http://sex-mission.com]=porn
  expectedCategories[http://sunnyfun.com]=notporn
  expectedCategories[http://yahoo.com]=notporn
  expectedCategories[http://youporn.com]=porn
  getActualPredictionData $expectedCategories

  getProbabilityDistribution $JobID
  declare -A expectedProbabilities 
  expectedProbabilities[notporn]=0.97945
  expectedProbabilities[porn]=0.02055
  getCategoryProbabilities $expectedProbabilities

  getPredictedWorkersQuality $JobID
  declare -A expectedWorkerQualities
  expectedWorkerQualities[worker1]=0.8516375211366187
  expectedWorkerQualities[worker2]=0.9011184676007533
  expectedWorkerQualities[worker3]=0.9749015350602155
  expectedWorkerQualities[worker4]=0.9749015350602155
  expectedWorkerQualities[worker5]=0.8439714531050402
  getWorkersQualityData $expectedWorkerQualities
  
  loadGoldLabels $JobID
  getPredictedWorkersQuality $JobID
  declare -A expectedWorkerQualities
  expectedWorkerQualities[worker1]=0.8516899142110332
  expectedWorkerQualities[worker2]=0.9011936513308119
  expectedWorkerQualities[worker3]=0.9749995850392734
  expectedWorkerQualities[worker4]=0.9749995850392734
  expectedWorkerQualities[worker5]=0.844066702629549
  getWorkersQualityData $expectedWorkerQualities
  
  deleteJob $JobID
}

function advancedTutorialPart3 {
  JobID=$(createJob)
  uploadAssignedLabels $JobID
  getJobStatus $redirectId "Assigns added" "Get assigned labels failed" "Got successfully job status for assigned labels"
  
  compute $JobID

  declare -A expectedDataCost
  expectedDataCost[http://google.com]=9.999500000000001E-5
  expectedDataCost[http://sex-mission.com]=1.3999019999999998E-4
  expectedDataCost[http://sunnyfun.com]=9.999500000000001E-5
  expectedDataCost[http://yahoo.com]=0.039238399199999996
  expectedDataCost[http://youporn.com]=1.3999019999999998E-4
  getDataCost $JobID ExpectedCost expectedDataCost
  
  deleteJob $JobID
}


advancedTutorialPart1
#advancedTutorialPart2
#advancedTutorialPart3


# function getPredictedObjectsCategories {
#   #get the prediction data
#   echo "Getting the prediction data ..."
#   local result=$(curl -s1 -X GET "$URL/jobs/$1/objects/prediction")
#   local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
#   redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)

#   if [[ "$status" != "OK" ]]
#     then
#       echo "ERROR: Get prediction data failed with status $status"
#       #exit 1
#     else
#       echo "Got successfully the prediction data"
#   fi
# }

# function getActualPredictedObjectsCategories {
#   #get the job status and check that the returned data is correct
#   echo "Getting actual predicted object categories job status for redirect=$redirectId ..."
#   local result=$(curl -s1 -X GET "$URL/$redirectId")
#   echo $result
#   local status=$(echo $result| cut -d '[' -f 2 | cut -d ']' -f 2 | cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
#   local predictionData=$(echo $result| cut -d '[' -f 2 | cut -d ']' -f 1)
#   echo "Received: $predictionData"

#   if [[ "$status" != "OK" ]]
#     then
#       echo "ERROR: Get prediction data job status failed with status $status"
#       #exit 1
#     else
#       echo "Got prediction data job status"
#   fi
  
#   for i in "${!expectedCategories[@]}"
#   do
#     if [[ "$predictionData" != *\"objectName\":"\"$i\",\"categoryName\":\"${expectedCategories[$i]}\""* ]]
#       then
# 	echo "ERROR: Could not find object "\"objectName\":\"$i\",\"categoryName\":\"${expectedCategories[$i]}\"" into the prediction data"
# 	#exit 1
#     fi
#   done

#   echo "-----------------"
# }

# function getProbabilityDistribution {

#   #get the category probability distribution
#   echo "Getting the category probability distribution data ..."
#   local result=$(curl -s1 -X GET "$URL/jobs/$1/data/http://yahoo.com/categoryProbability")
#   local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
#   redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)

#   if [[ "$status" != "OK" ]]
#     then
#       echo "ERROR: Get category probabilty failed with status $status"
#       #exit 1
#     else
#       echo "Got successfully category probability distribution data"
#   fi
# }


# function getCategoryProbabilities {
#   #get the job status and check that the data is correct
#   echo "Getting category probability job status for redirect=$redirectId..."
#   local result=$(curl -s1 -X GET "$URL/$redirectId")
#   local status=$(echo $result| cut -d '[' -f 2 | cut -d ']' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
#   local categProbabilityData=$(echo $result| cut -d '[' -f 2 | cut -d ']' -f 1)
#   echo "Received:  $categProbabilityData"

#   if [[ "$status" != "OK" ]]
#     then
#       echo "ERROR: Get category probability job status failed with status $status"
#       #exit 1
#     else
#       echo "Got category probability job status"
#   fi
  
#   for i in "${!expectedProbabilities[@]}"
#   do
#     if [[ "$categProbabilityData" != *\"categoryName\":"\"$i\",\"value\":${expectedProbabilities[$i]}"* ]]
#       then
# 	echo "ERROR: Could not find object "\"categoryName\":\"$i\",\"value\":${expectedProbabilities[$i]}" into the category probability data"
# 	#exit 1
#     fi
#   done
#   echo "-----------------"
# }


# function getPredictedWorkersQuality {
#   #get the workers quality
#   echo "Getting predicted workers quality ..."
#   local result=$(curl -s1 -X GET "$URL/jobs/$1/prediction/workersQuality")
#   local status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
#   redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)

#   if [[ "$status" != "OK" ]]
#     then
#       echo "ERROR: Get predicted worker quality failed with status $status"
#       #exit 1
#     else
#       echo "Got successfully predicted worker quality"
#   fi
# }

# function getWorkersQualityData {
#   #get the job status and check that the data is correct
#   echo "Getting workers quality job status for redirect=$redirectId..."
#   local result=$(curl -s1 -X GET "$URL/$redirectId")
#   local status=$(echo $result| cut -d '[' -f 2 | cut -d ']' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
#   local workerQualityData=$(echo $result| cut -d '[' -f 2 | cut -d ']' -f 1)
#   echo "Received: $workerQualityData"

#   if [[ "$status" != "OK" ]]
#     then
#       echo "ERROR: Get worker quality job status failed with status $status"
#       #exit 1
#     else
#       echo "Got workers quality job status"
#   fi
  
#   for i in "${!expectedWorkerQualities[@]}"
#   do
#     if [[ "$workerQualityData" != *\"workerName\":"\"$i\",\"value\":${expectedWorkerQualities[$i]}"* ]]
#       then
# 	echo "ERROR: Could not find object "\"workerName\":\"$i\",\"value\":${expectedWorkerQualities[$i]}" into the worker quality data"
# 	#exit 1
#     fi
#   done
#   echo "-----------------"
# }

# function getDataCost {
#   echo "Getting data cost for costAlgorithm=$2 ..."
#   local result=$(curl -s1 -X GET "$URL/jobs/$1/prediction/dataCost" -d "costAlgorithm=$2")
#   redirectId=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
  
#   echo "Getting the actual data cost for redirect=$redirectId..."
#   local result=$(curl -s1 -X GET "$URL/$redirectId")
#   local status=$(echo $result| cut -d '[' -f 2 | cut -d ']' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
#   local dataCost=$(echo $result| cut -d '[' -f 2 | cut -d ']' -f 1)
#   echo "Received: $dataCost"

#   if [[ "$status" != "OK" ]]
#     then
#       echo "ERROR: Get data cost job status failed with status $status"
#       #exit 1
#     else
#       echo "Got data cost job status"
#   fi
  
#   for i in "${!expectedDataCost[@]}"
#   do
#     if [[ "$dataCost" != *"\"objectName\":\"$i\",\"value\":${expectedDataCost[$i]}"* ]]
#       then
# 	echo "ERROR: Could not find object "\"objectName\":\"$i\",\"value\":${expectedDataCost[$i]}" into the dataCost data"
# 	#exit 1
#     fi
#   done
#   echo "-----------------"
# }
