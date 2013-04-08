#!/bin/bash 

source ./gal_tutorial_commons.sh

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

function main
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

main
