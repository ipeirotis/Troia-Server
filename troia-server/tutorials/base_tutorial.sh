#!/bin/bash

source ./base_tutorial_calls.sh

URL="http://localhost:8080/troia-server-1.1"
NO_ITERATIONS=20
COST_ALGORITHM=ExpectedCost
LABELCHOOSINGMETHOD=MaxLikelihood

function testConfusionMatrices
{
    local response=$1
    declare -A expected
    expected[worker1]='[{"from":"notporn","to":"notporn","value":0.0},{"from":"porn","to":"porn","value":1.0},{"from":"notporn","to":"porn","value":1.0},{"from":"porn","to":"notporn","value":0.0}]'
    expected[worker2]='[{"from":"notporn","to":"notporn","value":0.6666666666666666},{"from":"porn","to":"porn","value":1.0},{"from":"notporn","to":"porn","value":0.3333333333333333},{"from":"porn","to":"notporn","value":0.0}]'
    expected[worker3]='[{"from":"notporn","to":"notporn","value":1.0},{"from":"porn","to":"porn","value":1.0},{"from":"notporn","to":"porn","value":0.0},{"from":"porn","to":"notporn","value":0.0}]'
    expected[worker4]='[{"from":"notporn","to":"notporn","value":1.0},{"from":"porn","to":"porn","value":1.0},{"from":"notporn","to":"porn","value":0.0},{"from":"porn","to":"notporn","value":0.0}]'
    expected[worker5]='[{"from":"notporn","to":"notporn","value":0.0},{"from":"porn","to":"porn","value":0.0},{"from":"notporn","to":"porn","value":1.0},{"from":"porn","to":"notporn","value":1.0}]'
    # Extract result from the response.
    local result=$(echo $response | jsonObjectProperty "result")
    # Extract number of items in the result.
    local length=$(echo $result | jsonLength)
    # Iterate over all items in the result and test.
    for i in $(seq 0 $((length-1)))
    do
        local worker=$(echo $result | jsonArrayElement $i)
        local workerName=$(echo $worker | jsonObjectProperty "workerName")
        local workerMatrix=$(echo $worker | jsonObjectProperty "value" | jsonObjectProperty "matrix")
        local actualWorkerMatrix=$(echo $workerMatrix | jsonSorted)
        local expectedWorkerMatrix=$(echo ${expected[${workerName}]} | jsonSorted)
        (assertEqual "$actualWorkerMatrix" "$expectedWorkerMatrix") || (echo "For worker $workerName" && exit 1)
    done
    echo "Test passed: confusion matrices"
}

function testWorkersQualities
{
    local response=$1

    declare -A expected
    expected[worker1]=0.0
    expected[worker2]=0.4444444444444444
    expected[worker3]=1.0
    expected[worker4]=1.0
    expected[worker5]=1.0

    # Extract result from the response.
    local result=$(echo $response | jsonObjectPropertyFixed "result")
    # Extract number of items in the result.
    local length=$(echo $result | jsonLength)
    # Iterate over all items in the result and test.
    for i in $(seq 0 $((length-1)))
    do
        local worker=$(echo $result | jsonArrayElement $i)
        local workerName=$(echo $worker | jsonObjectProperty "workerName")
        local workerQuality=$(echo $worker | jsonObjectProperty "value")
        (assertEqual "$workerQuality" "${expected[${workerName}]}") || (echo "For worker $workerName" && exit 1)
    done
    echo "Test passed: workers qualities"
}

function testObjectsCategories
{
    local response=$1

    declare -A expected
    expected[http://google.com]=notporn
    expected[http://sex-mission.com]=porn
    expected[http://sunnyfun.com]=notporn
    expected[http://yahoo.com]=notporn
    expected[http://youporn.com]=porn

    # Extract result from the response.
    local result=$(echo $response | jsonObjectProperty "result")
    # Extract number of items in the result.
    local length=$(echo $result | jsonLength)
    # Iterate over all items in the result and test.
    for i in $(seq 0 $((length-1)))
    do
        local object=$(echo $result | jsonArrayElement $i)
        local objectName=$(echo $object | jsonObjectProperty "objectName")
        local categoryName=$(echo $object | jsonObjectProperty "categoryName")
        (assertEqual "$categoryName" "${expected[${objectName}]}") || (echo "For object $objectName" && exit 1)
    done
    echo "Test passed: objects categories"
}

function testObjectCategoryProbability
{
    local response=$1

    declare -A expected
    expected[porn]=0.0
    expected[notporn]=1.0
    
    # Extract result from the response.
    local result=$(echo $response | jsonObjectProperty "result")
    # Extract number of items in the result.
    local length=$(echo $result | jsonLength)
    # Iterate over all items in the result and test.
    for i in $(seq 0 $((length-1)))
    do
        local distribution=$(echo $result | jsonArrayElement $i)
        local categoryName=$(echo $distribution | jsonObjectProperty "categoryName")
        local categoryVal=$(echo  $distribution | jsonObjectProperty "value")
        (assertEqual "$categoryVal" "${expected[${categoryName}]}") || (echo "For object $categoryName" && exit 1)
    done
    echo "Test passed: object distribution"
}

# Main flow.

# Create a job.
response=$(createJob)
assertStatus "$response"

# Extract job id from the response.
jid=$(echo $response | cut -d ',' -f 2 | cut -d ':' -f 3 | cut -d '"' -f 1 | tr -d ' ')

testAsyncJobCall "loadAssignedLabels" "$jid" "Assigns added"
testAsyncJobCall "loadGoldLabels"     "$jid" "Objects added"
testAsyncJobCall "compute"            "$jid" "Computation done"

# testAsyncJobCallResults "getConfusionMatrices"         "$jid" "testConfusionMatrices"
# testAsyncJobCallResults "getWorkersQualities"          "$jid" "testWorkersQualities"
# testAsyncJobCallResults "getObjectsCategories"         "$jid" "testObjectsCategories"
# testAsyncJobCallResults "getObjectCategoryProbability" "$jid" "testObjectCategoryProbability"

response=$(deleteJob "$jid")
assertStatus "$response"
