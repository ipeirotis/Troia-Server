source ./source.sh

response='{"timestamp":"2013-04-06T23:22:18.245+02:00","result":[{"workerName":"worker2","value":{"rowDenominator":[{"categoryName":"porn","value":1.0},{"categoryName":"notporn","value":1.0}],"matrix":[{"from":"notporn","to":"notporn","value":0.6666666666666666},{"from":"porn","to":"porn","value":1.0},{"from":"notporn","to":"porn","value":0.3333333333333333},{"from":"porn","to":"notporn","value":0.0}],"categories":["porn","notporn"]}},{"workerName":"worker1","value":{"rowDenominator":[{"categoryName":"porn","value":1.0},{"categoryName":"notporn","value":1.0}],"matrix":[{"from":"notporn","to":"notporn","value":0.0},{"from":"porn","to":"porn","value":1.0},{"from":"notporn","to":"porn","value":1.0},{"from":"porn","to":"notporn","value":0.0}],"categories":["porn","notporn"]}},{"workerName":"worker5","value":{"rowDenominator":[{"categoryName":"porn","value":1.0},{"categoryName":"notporn","value":1.0}],"matrix":[{"from":"notporn","to":"notporn","value":0.0},{"from":"porn","to":"porn","value":0.0},{"from":"notporn","to":"porn","value":1.0},{"from":"porn","to":"notporn","value":1.0}],"categories":["porn","notporn"]}},{"workerName":"worker4","value":{"rowDenominator":[{"categoryName":"porn","value":1.0},{"categoryName":"notporn","value":1.0}],"matrix":[{"from":"notporn","to":"notporn","value":1.0},{"from":"porn","to":"porn","value":1.0},{"from":"notporn","to":"porn","value":0.0},{"from":"porn","to":"notporn","value":0.0}],"categories":["porn","notporn"]}},{"workerName":"worker3","value":{"rowDenominator":[{"categoryName":"porn","value":1.0},{"categoryName":"notporn","value":1.0}],"matrix":[{"from":"notporn","to":"notporn","value":1.0},{"from":"porn","to":"porn","value":1.0},{"from":"notporn","to":"porn","value":0.0},{"from":"porn","to":"notporn","value":0.0}],"categories":["porn","notporn"]}}],"executionTime":0.0,"status":"OK"}'
echo $(echo $response | jsonObjectProperty "timestamp")
echo $(echo $response | jsonObjectProperty "status")
echo $(echo $response | jsonObjectPropertyFixed "result")
echo $(echo $response | jsonObjectProperty "result")

response='{"timestamp":"2013-04-07T21:06:11.088+02:00","result":[{"workerName":"worker2","value":0.4444444444444444},{"workerName":"worker3","value":1.0},{"workerName":"worker4","value":1.0},{"workerName":"worker5","value":1.0},{"workerName":"worker1","value":0.0}],"status":"OK"}'
echo $(echo $response | jsonObjectProperty "timestamp")
echo $(echo $response | jsonObjectProperty "status")
echo $(echo $response | jsonObjectPropertyFixed "result")
echo $(echo $response | jsonObjectProperty "result")
