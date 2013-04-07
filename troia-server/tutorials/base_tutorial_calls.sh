source ./source.sh

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
        algorithm: "BDS",
        iterations: 10,
        epsilon: 0.0001,
        scheduler: "NormalScheduler",
        calculator: "CostBased"
    }')
}

function deleteJob 
{
    local jid=$1
    echo $(curl -s1 -X DELETE -H "Content-Type: application/json" "$URL/jobs" -d "id=$jid")
}

function loadAssignedLabels 
{
    local jid=$1
    echo $(curl -s1 -X POST -H "Content-Type: application/json" "$URL/jobs/$jid/assigns" -d '{
        assigns:
            [{
                "worker":"worker1",
                "object":"http://sunnyfun.com",
                "label":"porn"
            },
            {
                "worker":"worker1",
                "object":"http://sex-mission.com",
                "label":"porn"
            },
            {
                "worker":"worker1",
                "object":"http://google.com",
                "label":"porn"
            },
            {
                "worker":"worker1",
                "object":"http://youporn.com",
                "label":"porn"
            },
            {
                "worker":"worker1",
                "object":"http://yahoo.com",
                "label":"porn"
            },
            {
                "worker":"worker2",
                "object":"http://sunnyfun.com",
                "label":"notporn"
            },
            {
                "worker":"worker2",
                "object":"http://sex-mission.com",
                "label":"porn"
            },
            {
                "worker":"worker2",
                "object":"http://google.com",
                "label":"notporn"
            },
            {
                "worker":"worker2",
                "object":"http://youporn.com",
                "label":"porn"
            },
            {
                "worker":"worker2",
                "object":"http://yahoo.com",
                "label":"porn"
            },
            {
                "worker":"worker3",
                "object":"http://sunnyfun.com",
                "label":"notporn"
            },
            {
                "worker":"worker3",
                "object":"http://sex-mission.com",
                "label":"porn"
            },
            {
                "worker":"worker3",
                "object":"http://google.com",
                "label":"notporn"
            },
            {
                "worker":"worker3",
                "object":"http://youporn.com",
                "label":"porn"
            },
            {
                "worker":"worker3",
                "object":"http://yahoo.com",
                "label":"notporn"
            },
            {
                "worker":"worker4",
                "object":"http://sunnyfun.com",
                "label":"notporn"
            },
            {
                "worker":"worker4",
                "object":"http://sex-mission.com",
                "label":"porn"
            },
            {
                "worker":"worker4",
                "object":"http://google.com",
                "label":"notporn"
            },
            {
                "worker":"worker4",
                "object":"http://youporn.com",
                "label":"porn"
            },
            {
                "worker":"worker4",
                "object":"http://yahoo.com",
                "label":"notporn"
            },
            {
                "worker":"worker5",
                "object":"http://sunnyfun.com",
                "label":"porn"
            },
            {
                "worker":"worker5",
                "object":"http://sex-mission.com",
                "label":"notporn"
            },
            {
                "worker":"worker5",
                "object":"http://google.com",
                "label":"porn"
            },
            {
                "worker":"worker5",
                "object":"http://youporn.com",
                "label":"notporn"
            },
            {
                "worker":"worker5",
                "object":"http://yahoo.com",
                "label":"porn"
            }]
        }'
    )
}

function loadGoldLabels
{
    local jid=$1
    echo $(curl -s1 -X POST -H "Content-Type: application/json" "$URL/jobs/$jid/goldObjects" -d '{
        objects:
            [{
                "goldLabel": "notporn",
                "name": "http://google.com"
            }]
        }'
    )
}

function compute
{
    local jid=$1
    echo $(curl -s1 -X POST -d "iterations=$NO_ITERATIONS" "$URL/jobs/$jid/compute")
}

function getConfusionMatrices
{
    local jid=$1
    echo $(curl -s1 -X GET "$URL/jobs/$jid/workers/quality/matrix")
}

function getWorkersQualities
{
    local jid=$1
    echo $(curl -s1 -X GET "$URL/jobs/$1/workers/quality/estimated" -d "{costAlgorithm: $COST_ALGORITHM}")
}

function getObjectsCategories
{
    local jid=$1
    echo $(curl -s1 -X GET "$URL/jobs/$1/objects/prediction" -d "{labelChoosing: $LABEL_CHOOSING_METHOD")
}

function getObjectCategoryProbability
{
    local jid=$1
    # local oid=$2
    local oid=http://sunnyfun.com
    echo $(curl -s1 -X GET "$URL/jobs/$jid/objects/$oid/categoryProbability")
}
