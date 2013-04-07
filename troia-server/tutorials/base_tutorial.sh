#!/bin/bash

source ./base_tutorial_calls.sh

URL="http://localhost:8080/troia-server-1.1"
NO_ITERATIONS=20
COST_ALGORITHM=ExpectedCost
LABELCHOOSINGMETHOD=MaxLikelihood

# Basic tutorial: main flow

# Create a job.
response=$(createJob)
assertStatus "$response"

# Extract job id from the response.
jid=$(echo $response | cut -d ',' -f 2 | cut -d ':' -f 3 | cut -d '"' -f 1 | tr -d ' ')

testAsyncJobCall "loadAssignedLabels" "$jid" "Assigns added"
testAsyncJobCall "loadGoldLabels"     "$jid" "Objects added"
testAsyncJobCall "compute"            "$jid" "Computation done"

expectedCategories='[
    {"objectName":"http://google.com","categoryName":"notporn"},
    {"objectName":"http://youporn.com","categoryName":"porn"},
    {"objectName":"http://sunnyfun.com","categoryName":"notporn"},
    {"objectName":"http://sex-mission.com","categoryName":"porn"},
    {"objectName":"http://yahoo.com","categoryName":"notporn"}
]'

expectedQualities='[
    {"workerName":"worker2","value":0.4444444444444444},
    {"workerName":"worker3","value":1.0},
    {"workerName":"worker4","value":1.0},
    {"workerName":"worker5","value":1.0},
    {"workerName":"worker1","value":0.0}
]'

expectedProbability='[
    {
        "categoryName": "notporn",
        "value": 1.0
    },
    {
        "categoryName": "porn",
        "value": 0.0
    }
]'

testAsyncJobCallResult "getObjectsCategories"         "$jid" "$expectedCategories"  
testAsyncJobCallResult "getWorkersQualities"          "$jid" "$expectedQualities"  
testAsyncJobCallResult "getObjectCategoryProbability" "$jid" "$expectedProbability"
# TODO prepare appropirate form of the expected confusion matrices
# corresponding to the current form of the server response or remove it,
# because it is not mentioned in the tutorial.
# function testConfusionMatrices
# {
#     local response=$1
#     declare -A expected
#     expected[worker1]='[{"from":"notporn","to":"notporn","value":0.0},{"from":"porn","to":"porn","value":1.0},{"from":"notporn","to":"porn","value":1.0},{"from":"porn","to":"notporn","value":0.0}]'
#     expected[worker2]='[{"from":"notporn","to":"notporn","value":0.6666666666666666},{"from":"porn","to":"porn","value":1.0},{"from":"notporn","to":"porn","value":0.3333333333333333},{"from":"porn","to":"notporn","value":0.0}]'
#     expected[worker3]='[{"from":"notporn","to":"notporn","value":1.0},{"from":"porn","to":"porn","value":1.0},{"from":"notporn","to":"porn","value":0.0},{"from":"porn","to":"notporn","value":0.0}]'
#     expected[worker4]='[{"from":"notporn","to":"notporn","value":1.0},{"from":"porn","to":"porn","value":1.0},{"from":"notporn","to":"porn","value":0.0},{"from":"porn","to":"notporn","value":0.0}]'
#     expected[worker5]='[{"from":"notporn","to":"notporn","value":0.0},{"from":"porn","to":"porn","value":0.0},{"from":"notporn","to":"porn","value":1.0},{"from":"porn","to":"notporn","value":1.0}]'
#     # Extract result from the response.
#     local result=$(echo $response | jsonObjectProperty "result")
#     # Extract number of items in the result.
#     local length=$(echo $result | jsonLength)
#     # Iterate over all items in the result and test.
#     for i in $(seq 0 $((length-1)))
#     do
#         local worker=$(echo $result | jsonArrayElement $i)
#         local workerName=$(echo $worker | jsonObjectProperty "workerName")
#         local workerMatrix=$(echo $worker | jsonObjectProperty "value" | jsonObjectProperty "matrix")
#         local actualWorkerMatrix=$(echo $workerMatrix | jsonSorted)
#         local expectedWorkerMatrix=$(echo ${expected[${workerName}]} | jsonSorted)
#         (assertEqual "$actualWorkerMatrix" "$expectedWorkerMatrix") || (echo "For worker $workerName" && exit 1)
#     done
#     echo "Test passed: confusion matrices"
# }
#testAsyncJobCallResults "getConfusionMatrices"         "$jid" "testConfusionMatrices"

response=$(deleteJob "$jid")
assertStatus "$response"
