PYTHON=python2.7
EXPECTED_STATUS="OK"

function jsonStrip
{
    local json=$1
    echo $(echo $json | sed "s/^\([\"']\)\(.*\)\1\$/\2/g")
}

function jsonObjectProperty
{
    local name=$1
    local result=$($PYTHON -c "import json; import sys; print json.dumps(json.load(sys.stdin)['$name'], separators=(',', ':'))")
    echo $(jsonStrip "$result")
}

# XXX nasty fix for very strange bug (reproduced within json_bug.sh).
function jsonObjectPropertyFixed
{
    local name=$1
    local result=$($PYTHON -c "import json; import sys; print json.dumps(json.load(sys.stdin)['$name'], separators=(', ', ':'))")
    echo $(jsonStrip "$result")
}

function jsonArrayElement
{
    local name=$1
    local result=$($PYTHON -c "import json; import sys; print json.dumps(json.load(sys.stdin)[$name], separators=(',', ':'))")
    echo $(jsonStrip "$result")
}

function jsonLength
{
    echo $($PYTHON -c "import json; import sys; print len(json.load(sys.stdin))")
}

function jsonSorted
{
    local result=$($PYTHON -c "import json; import sys; print json.dumps(sorted(json.load(sys.stdin)), separators=(',', ':'))")
    echo $(jsonStrip "$result")
}

function responseStatus
{
    local response=$1
    echo $(echo $response | jsonObjectProperty "status")
}

function responseResult
{
    local response=$1
    echo $(echo $response | jsonObjectProperty "result")
}

function assertEqual
{
    local actual=$(echo $1)
    local expected=$2
    if [ "$actual" != "$expected" ]
    then
        echo "Assertion error:"
        echo "Actual: $actual"
        echo "Expected: $expected"
        exit 1
    fi
    exit 0
}

function assertStatus
{
    local response=$1
    local status=$(responseStatus "$response")
    (assertEqual "$status" "$EXPECTED_STATUS") || echo "Error status is not $EXPECTED_STATUS in response:" || echo $response
}

function assertResult
{
    local response=$1
    local expectedResult=$2
    local result=$(responseResult "$response")
    (assertEqual "$result" "$expectedResult") || echo "Error in response:" || echo $response
}

function awaitCompletion
{
    local response=$1
    local redirection=$(echo $response | jsonObjectProperty "redirect")
    local result=$(curl -s1 -H "Content-Type: application/json" "$URL/$redirection")
    while true
    do
        local status=$(responseStatus "$response")
        # TODO maybe it should be more generic
        if [ "$status" = "OK" ] || [ "$status" = "ERROR" ]
        then
            break;
        fi
        sleep 5
        local result=$(curl -s1 -H "Content-Type: application/json" "$URL/$redirection")
    done
    echo $result
}

# Test job call. 
function testAsyncJobCall
{
    local cmd=$1
    local jid=$2
    local response=$($cmd $jid)
    assertStatus "$response"
    local result=$(awaitCompletion "$response")
    assertStatus "$result" 
    if [ "$#" -gt 2 ]
    then
        local expectedResult=$3
        assertResult "$result" "$expectedResult"
    fi
    echo $result
}

function testAsyncJobCallResults
{
    local cmd=$1
    local jid=$2
    local tester=$3
    local result=$(testAsyncJobCall "$cmd" "$jid")
    echo $($tester $result)
}
