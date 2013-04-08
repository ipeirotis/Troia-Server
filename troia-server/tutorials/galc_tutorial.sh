#!/bin/bash

source ./source.sh

function createJob
{
    echo $(curl -s1 -X POST -H "Content-Type: application/json" "$URL/cjobs")
}

function deleteJob 
{
    local jid=$1
    echo $(curl -s1 -X DELETE -H "Content-Type: application/json" "$URL/cjobs" -d "id=$jid")
}

function loadAssignedLabels 
{
    local jid=$1
    echo $(curl -s1 -X POST -H "Content-Type: application/json" "$URL/cjobs/$jid/assigns" -d '{"assigns":
        [{
            "worker":"worker1",
            "object":"http://sunnyfun.com",
            "label":{"value":4.399898705211159}
        },
        {
            "worker":"worker1",
            "object":"http://sex-mission.com",
            "label":{"value":-2.2209043312752503}
        },
        {
            "worker":"worker1",
            "object":"http://google.com",
            "label":{"value":0.2501928056080521}
        },
        {
            "worker":"worker1",
            "object":"http://youporn.com",
            "label":{"value":2.630393454314285}
        },
        {
            "worker":"worker1",
            "object":"http://yahoo.com",
            "label":{"value":1.7745486537318291}
        },
        {
            "worker":"worker2",
            "object":"http://sunnyfun.com",
            "label":{"value":2.0909755923562328}
        },
        {
            "worker":"worker2",
            "object":"http://sex-mission.com",
            "label":{"value":2.879992076430761}
        },
        {
            "worker":"worker2",
            "object":"http://google.com",
            "label":{"value":-0.800554365150114}
        },
        {
            "worker":"worker2",
            "object":"http://youporn.com",
            "label":{"value":-1.5672083199519249}
        },
        {
            "worker":"worker2",
            "object":"http://yahoo.com",
            "label":{"value":-1.2928498723416584}
        }]}'
    )
}

function loadGoldLabels 
{
    local jid=$1
    echo $(curl -s1 -X POST -H "Content-Type: application/json" "$URL/cjobs/$jid/goldObjects" -d '{"objects":
        [{
            "name": "http://google.com",
            "goldLabel":{"value":10.219077484951955,"zeta":0.292643407722905}
        },
            {
            "name": "http://sunnyfun.com",
            "goldLabel":{"value":8.219077484951955,"zeta":0.343407722905}
        }]}'
    )
}

function loadUnassignedLabels
{
    local jid=$1
    echo $(curl -s1 -X POST -H "Content-Type: application/json" "$URL/cjobs/$jid/objects" -d '{"objects":
        [
            {"name":"object1"},
            {"name":"object2"}
        ]}'
    )
}

function compute
{
    local jid=$1
    echo $(curl -s1 -X POST "$URL/cjobs/$jid/compute")
}

function getObjectsPrediction
{
    local jid=$1
    echo $(curl -s1 -X GET "$URL/cjobs/$jid/objects/prediction")
}

function getWorkersQualities
{
    local jid=$1
    echo $(curl -s1 -X GET "$URL/cjobs/$jid/workers/quality/estimated")
}

function galcTutorial
{
    # Create a job.
    response=$(createJob)
    assertStatus "$response"

    # Extract job id from the response.
    jid=$(echo $response | cut -d ',' -f 2 | cut -d ':' -f 3 | cut -d '"' -f 1 | tr -d ' ')

    testAsyncJobCall "loadAssignedLabels"   "$jid" "Assigns added"
    testAsyncJobCall "loadGoldLabels"       "$jid" "Objects added"
    testAsyncJobCall "loadUnassignedLabels" "$jid" "Objects added"
    testAsyncJobCall "compute"              "$jid" "Computation done"

    expectedObjectsPrediction='[
        {
            "est_value":0.11456418171572877,
            "est_zeta":-0.1913547099132667,
            "distributionMu":0.22044451213182,
            "distributionSigma":0.5533196986062296,
            "object":{"name":"http://youporn.com"}
        },
        {
            "est_value":0.41045876986866625,
            "est_zeta":0.343407722905,
            "distributionMu":0.22044451213182,
            "distributionSigma":0.5533196986062296,
            "object": 
            {
                "name":"http://sunnyfun.com",
                "goldLabel":
                {
                    "value":8.219077484951955,
                    "zeta":0.343407722905
                }
            }
        },
        {
            "est_value":0.17414895890312143,
            "est_zeta":-0.08366872414864963,
            "distributionMu":0.22044451213182,
            "distributionSigma":0.5533196986062296,
            "object":{"name":"http://sex-mission.com"}
        },
        {
            "est_value":0.38236987429215774,
            "est_zeta":0.292643407722905,
            "distributionMu":0.22044451213182,
            "distributionSigma":0.5533196986062296,
            "object":
            {
                "name":"http://google.com",
                "goldLabel":
                {
                    "value":10.219077484951955,
                    "zeta":0.292643407722905
                }
            }
        },
        {
            "est_value":NaN,
            "est_zeta":NaN,
            "distributionMu":0.22044451213182,
            "distributionSigma":0.5533196986062296,
            "object":{"name":"object1"}
        },
        {
            "est_value":NaN,
            "est_zeta":NaN,
            "distributionMu":0.22044451213182,
            "distributionSigma":0.5533196986062296,
            "object":{"name":"object2"}
        },
        {
            "est_value":0.05636583076319032,
            "est_zeta":-0.2965350443548846,
            "distributionMu":0.22044451213182,
            "distributionSigma":0.5533196986062296,
            "object":{"name":"http://yahoo.com"}
        }
    ]'

    expectedWorkersQualities='[
        {
            "est_rho":-0.2706672409621047,
            "est_mu":0.26207102226865925,
            "est_sigma":1.8488708546932122,
            "zeta":[
            {
                "worker":"worker2",
                "object":"http://youporn.com",
                "label":{"value":-0.9894035257125199}
            },
            {
                "worker":"worker2",
                "object":"http://yahoo.com",
                "label":{"value":-0.8410110909927939}
            },
            {
                "worker":"worker2",
                "object":"http://google.com",
                "label":{"value":-0.5747428949520097}
            },
            {
                "worker":"worker2",
                "object":"http://sex-mission.com",
                "label":{"value":1.4159566891958497}
            },
            {
                "worker":"worker2",
                "object":"http://sunnyfun.com",
                "label":{"value":0.9892008224614738}
            }
            ],
            "worker":"worker2"
        },
        {
            "est_rho":0.2706672409621048,
            "est_mu":1.366825857518015,
            "est_sigma":2.2396896717998964,
            "zeta":[
            {
                "worker":"worker1",
                "object":"http://google.com",
                "label":{"value":-0.4985659691918817}
            },
            {
                "worker":"worker1",
                "object":"http://youporn.com",
                "label":{"value":0.5641708370163716}
            },
            {
                "worker":"worker1",
                "object":"http://sex-mission.com",
                "label":{"value":-1.60188718730396}
            },
            {
                "worker":"worker1",
                "object":"http://yahoo.com",
                "label":{"value":0.18204432575971705}
            },
            {
                "worker":"worker1",
                "object":"http://sunnyfun.com",
                "label":{"value":1.354237993719753}
            }
            ],
            "worker":"worker1"
        }
    ]'

    testAsyncJobCallResult "getObjectsPrediction" "$jid" "$expectedObjectsPrediction"
    testAsyncJobCallResult "getWorkersQualities"  "$jid" "$expectedWorkersQualities"

    response=$(deleteJob "$jid")
    assertStatus "$response"
}

galcTutorial
