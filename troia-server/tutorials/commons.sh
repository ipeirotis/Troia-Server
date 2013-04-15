PYTHON=python2.7
EXPECTED_STATUS="OK"
URL="http://localhost:8080/troia-server-1.1"

function jsonStrip
{
    local json=$1
    echo $(echo $json | sed "s/^\([\"']\)\(.*\)\1\$/\2/g")
}

function jsonObjectProperty
{
    local name=$1
    local result=$($PYTHON -c "import json; import sys; print json.dumps(json.load(sys.stdin)['$name'])")
    echo $(jsonStrip "$result")
}

function jsonArrayElement
{
    local name=$1
    local result=$($PYTHON -c "import json; import sys; print json.dumps(json.load(sys.stdin)[$name])")
    echo $(jsonStrip "$result")
}

function jsonLength
{
    echo $($PYTHON -c "import json; import sys; print len(json.load(sys.stdin))")
}

function jsonSorted
{
    local result=$($PYTHON -c "import json; import sys; print json.dumps(sorted(json.load(sys.stdin)))")
    echo $(jsonStrip "$result")
}

function jsonEquals
{
    local json1=$1
    local json2=$2
    $($PYTHON -c "
import json
import sys

def parse(js):
    try:
        result = json.loads(js)
    except ValueError:
        result = json.loads('\"' + js + '\"')
    return result

json1 = '''$json1'''
json2 = '''$json2'''

if json.dumps(sorted(parse(json1))) == json.dumps(sorted(parse(json2))):
    sys.exit(0)
else:
    sys.exit(1)
    ")
}

function jsonFormat
{
    local result=$($PYTHON -c "import json; import sys; print json.dumps(json.load(sys.stdin))")
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

function fetchSyncJobCallResponse
{
    local cmd=$1
    local jid=$2
    local response=$($cmd $jid)
    echo $response | $PYTHON -mjson.tool
}

function fetchAsyncJobCallResponse
{
    local cmd=$1
    local jid=$2
    local response=$(fetchSyncJobCallResponse "$cmd" "$jid")
    assertStatus "$response"
    local result=$(awaitCompletion "$response")
    echo $result | $PYTHON -mjson.tool
}

function testAsyncJobCallResponse
{
    local cmd=$1
    local jid=$2
    local expectedResult=$3
    local response=$($cmd $jid)
    assertStatus "$response"
    local result=$(awaitCompletion "$response")
    assertStatus "$result" 
    local innerResult=$(echo $result | jsonObjectProperty "result")
    local expectedInnerResult=$(echo $expectedResult | jsonObjectProperty "result")
    ((jsonEquals "$innerResult" "$expectedInnerResult") && echo "$cmd OK") || (
        echo "Assertion error. Results are not the same:" &&
        echo "Actual:   $innerResult" &&
        echo "Expected: $expectedInnerResult"
    )
}

# Version for debugging
function testAsyncJobCallResponse_
{
    local cmd=$1
    local jid=$2
    local expectedResult=$3
    local response=$($cmd $jid)
    assertStatus "$response"
    local result=$(awaitCompletion "$response")
    assertStatus "$result" 
    local innerResult=$(echo $result | jsonObjectProperty "result")
    local expectedInnerResult=$(echo $expectedResult | jsonObjectProperty "result")
    ((jsonEquals "$innerResult" "$expectedInnerResult") && echo "$cmd OK") || (
        echo "Assertion error. Results are not the same:" &&
        echo "Actual:   $innerResult" &&
        echo "Expected: $expectedInnerResult"
    )
    # Simple debugging code
    echo "$innerResult" | $PYTHON -mjson.tool > act.json
    echo "$expectedInnerResult" | $PYTHON -mjson.tool > exp.json
}
