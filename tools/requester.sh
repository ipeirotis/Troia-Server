#!/bin/bash

W=`curl -s --request $@`
echo $W | python -mjson.tool 2> /dev/null
if [ $? -ne 0 ]; then
    echo $W
fi

