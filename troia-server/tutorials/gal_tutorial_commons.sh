source ./commons.sh

# Common calls

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

# Common responses

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

