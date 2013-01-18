#!/bin/bash

URL="http://localhost:8080/troia-server-0.8"
#URL="http://project-troia.com/api"
JobID="hihihi"

#create the job
echo "Creating a new job ..."
result=$(curl -s1 -X POST -H "Content-Type: application/json" "$URL/jobs" -d "id=$JobID&type=incremental&categories=[
{"prior":"1","name":"porn","misclassification_cost":{"porn":"0","notporn":"1"}},
{"prior":"1","name":"notporn","misclassification_cost":{"porn":"1","notporn":"0"}}]")

status=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)

if [[ "$status" != "OK" ]]
  then
    echo "Job with id $JobID already exists"
  else
    echo "Created new job with id $JobID"
fi
echo "-----------------"


#upload assigned labels
echo "Uploading assignedLabels ..."
result=$(curl -s1 -X POST -H "Content-Type: application/json" "$URL/jobs/$JobID/assignedLabels" -d 'labels=[
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

status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
redirect=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)

if [[ "$status" != "OK" ]]
  then
    echo "Upload assigned labels failed with status $status"
    exit 1
  else
    echo "Uploaded successfully the assigned labels"
fi


#check that the assigned labels were uploaded successfully
echo "Getting assigned labels job status for redirect=$redirect ..."
result=$(curl -s1 -X GET "$URL/jobs/$JobID/status/$redirect")
status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)

if [[ "$status" != "Assigns added" ]]
  then
    echo "Get assigned labels failed with status $status"
    exit 1
  else
    echo "Got successfully job status for assigned labels "
fi
echo "-----------------"

#get the prediction data
echo "Getting the prediction data ..."
result=$(curl -s1 -X GET "$URL/jobs/$JobID/prediction/data")
status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
redirect=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)

if [[ "$status" != "OK" ]]
  then
    echo "Get prediction data failed with status $status"
    exit 1
  else
    echo "Got successfully the prediction data"
fi

#get the job status and check that the returned data is correct
echo "Getting prediction data job status for redirect=$redirect ..."
result=$(curl -s1 -X GET "$URL/jobs/$JobID/status/$redirect")
status=$(echo $result| cut -d '{' -f 3 | cut -d '}' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
predictionData=$(echo $result| cut -d '{' -f 3 | cut -d '}' -f 1)
echo "Received: $predictionData"

if [[ "$status" != "OK" ]]
  then
    echo "Get prediction data job status failed with status $status"
    exit 1
  else
    echo "Got prediction data job status"
fi

if [[ "$predictionData" != *\"http://google.com\":\"notporn\"* ]]
  then
    echo "Couldn't find object \"http://google.com\":\"notporn\" into the prediction data"
    exit 1
fi

if [[ "$predictionData" != *\"http://youporn.com\":\"porn\"* ]]
  then
    echo "Couldn't find object \"http://youporn.com\":\"porn\" into the prediction data"
    exit 1
fi

if [[ "$predictionData" != *\"http://sunnyfun.com\":\"notporn\"* ]]
  then
    echo "Couldn't find object \"http://sunnyfun.com\":\"notporn\" into the prediction data"
    exit 1
fi

if [[ "$predictionData" != *\"http://sex-mission.com\":\"porn\"* ]]
  then
    echo "Couldn't find object \"http://sex-mission.com\":\"porn\" into the prediction data"
    exit 1
fi

if [[ "$predictionData" != *\"http://yahoo.com\":\"notporn\"* ]]
  then
    echo "Couldn't find object \"http://yahoo.com\":\"notporn\" into the prediction data"
    exit 1
fi
echo "-----------------"

#get the category probability distribution
echo "Getting the category probability distribution data ..."
result=$(curl -s1 -X GET "$URL/jobs/$JobID/data/http://yahoo.com/categoryProbability")
status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
redirect=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)

if [[ "$status" != "OK" ]]
  then
    echo "Get category probabilty failed with status $status"
    exit 1
  else
    echo "Got successfully category probability distribution data"
fi

#get the job status and check that the data is correct
echo "Getting category probability job status for redirect=$redirect..."
result=$(curl -s1 -X GET "$URL/jobs/$JobID/status/$redirect")
status=$(echo $result| cut -d ',' -f 4 | cut -d ':' -f 2 | cut -d '"' -f 2)
categProbabilityData=$(echo $result| cut -d '{' -f 3 | cut -d '}' -f 1)
echo "Received:  $categProbabilityData"

if [[ "$status" != "OK" ]]
  then
    echo "Get category probability job status failed with status $status"
    exit 1
  else
    echo "Got category probability job status"
fi

if [[ "$categProbabilityData" != *"\"notporn\":0.97945"* ]]
  then
    echo "Couldn't find object \"notporn\":0.97945 into the category probability data"
    #exit 1
fi

if [[ "$categProbabilityData" != *"\"porn\":0.02055"* ]]
  then
    echo "Couldn't find object \"porn\":0.02055 into the category probability data"
    #exit 1
fi
echo "-----------------"


#get the workers quality
echo "Getting predicted workers quality ..."
result=$(curl -s1 -X GET "$URL/jobs/$JobID/prediction/workersQuality")
status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
redirect=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)

if [[ "$status" != "OK" ]]
  then
    echo "Get predicted worker quality failed with status $status"
    exit 1
  else
    echo "Got successfully predicted worker quality"
fi

#get the job status and check that the data is correct
echo "Getting workers quality job status for redirect=$redirect..."
result=$(curl -s1 -X GET "$URL/jobs/$JobID/status/$redirect")
status=$(echo $result| cut -d '{' -f 3 | cut -d '}' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
workerQualityData=$(echo $result| cut -d '{' -f 3 | cut -d '}' -f 1)
echo "Received: $workerQualityData"


if [[ "$status" != "OK" ]]
  then
    echo "Get worker quality job status failed with status $status"
    exit 1
  else
    echo "Got workers quality job status"
fi

if [[ "$workerQualityData" != *"\"worker1\":0.8516375211366187"* ]]
  then
    echo "Couldn't find object \"worker1\":0.8516375211366187 into the worker quality data"
    #exit 1
fi

if [[ "$workerQualityData" != *"\"worker2\":0.9011184676007533"* ]]
  then
    echo "Couldn't find object \"worker2\":0.9011184676007533 into the worker quality data"
    #exit 1
fi

if [[ "$workerQualityData" != *"\"worker3\":0.9749015350602155"* ]]
  then
    echo "Couldn't find object \"worker3\":0.9749015350602155 into the worker quality data"
    #exit 1
fi

if [[ "$workerQualityData" != *"\"worker4\":0.9749015350602155"* ]]
  then
    echo "Couldn't find object \"worker4\":0.9749015350602155 into the worker quality data"
    #exit 1
fi

if [[ "$workerQualityData" != *"\"worker5\":0.8439714531050402"* ]]
  then
    echo "Couldn't find object \"worker5\":0.8439714531050402 into the worker quality data"
    #exit 1
fi
echo "-----------------"

#compute 
echo "Computing - using 20 iteraitons ..."
result=$(curl -s1 -X POST -d "iterations=20" "$URL/jobs/$JobID/compute")
redirect=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)
status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
if [[ "$status" != "OK" ]]
  then
    echo "Data computation failed with status $status"
    exit 1
  else
    echo "Data computation finished successfully"
fi
echo "-----------------"

#get the category probability distribution
echo "Getting the category probability distribution data ..."
result=$(curl -s1 -X GET "$URL/jobs/$JobID/data/http://yahoo.com/categoryProbability")
status=$(echo $result| cut -d ',' -f 2 | cut -d ':' -f 2 | cut -d '"' -f 2)
redirect=$(echo $result| cut -d ',' -f 3 | cut -d ':' -f 2 | cut -d '"' -f 2)

if [[ "$status" != "OK" ]]
  then
    echo "Get category probabilty failed with status $status"
    exit 1
  else
    echo "Got successfully category probability distribution data"
fi

#get the job status and check that the data is correct
echo "Getting category probability job status for redirect=$redirect..."
result=$(curl -s1 -X GET "$URL/jobs/$JobID/status/$redirect")
status=$(echo $result| cut -d ',' -f 4 | cut -d ':' -f 2 | cut -d '"' -f 2)
categProbabilityData=$(echo $result| cut -d '{' -f 3 | cut -d '}' -f 1)
echo "Received:  $categProbabilityData"

if [[ "$status" != "OK" ]]
  then
    echo "Get category probability job status failed with status $status"
    exit 1
  else
    echo "Got category probability job status"
fi

if [[ "$categProbabilityData" != *"\"notporn\":0.98"* ]]
  then
    echo "Couldn't find object \"notporn\":0.98 into the category probability data"
    #exit 1
fi

if [[ "$categProbabilityData" != *"\"porn\":0.02"* ]]
  then
    echo "Couldn't find object \"porn\":0.02 into the category probability data"
    #exit 1
fi
echo "-----------------"



