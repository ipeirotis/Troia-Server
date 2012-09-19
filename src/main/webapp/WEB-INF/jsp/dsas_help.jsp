<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>Web-service com.datascience.gal.service.Service documentation</title>
		<script src="http://code.jquery.com/jquery-latest.js"></script>
		<style>
			body {
				margin: 0;
				padding: 0;
			}
			#header h1, #header h2, #header p {
				margin-left: 2%;
				padding-right: 2%;
			}
			#active2 #tab2, #active3 #tab3, #active4 #tab4, #active5 #tab5 {
				color: #000000;
				font-weight: bold;
				text-decoration: none;
			}
			#footer {
				clear: both;
				float: left;
				width: 100%;
			}
			#footer p {
				margin-left: 2%;
				padding-right: 2%;
			}
			#container2 {
				background: none repeat scroll 0 0 #EDF1F3;
				clear: left;
				float: left;
				overflow: hidden;
				width: 100%;
			}
			#container1 {
				background: none repeat scroll 0 0 #FBFBFB;
				float: left;
				position: relative;
				right: 50%;
				width: 100%;
			}
			#col1 {
				float: left;
				left: 52%;
				overflow: hidden;
				position: relative;
				width: 46%;
				font-family : 'Palatino Linotype','Book Antiqua',Palatino,FreeSerif,serif;
				font-size : 15px;
			}
			#col2 {
				float: left;
				left: 56%;
				overflow: hidden;
				position: relative;
				width: 46%;
				font-family : Monaco,Consolas,"Lucida Console",monospace;
				font-size : 12px;
			}
			div.textright {
				padding-top : 10px;
				padding-bottom : 0px;
				font-family : monospace;
				font-size : 15px;
			}
			div.label1right {
				font-family : Helvetica,Arial,sans-serif;
				font-size : 18px;
				color : #69697D
			}
			div.label0left {
				font-family : 'Palatino Linotype','Book Antiqua',Palatino,FreeSerif,serif;
				font-size : 27px;
			}
			span.greyhead0right {
				color:#696969;
			}
			div.greyhead1right {
				padding-top : 20px;
				padding-bottom : 10px;
				color:#696969;
			}
			span.greyhead2right {
				display: block;
				padding-top : 0px;
				padding-bottom : 0px;
				padding-left:30px;
				color:#696969;
				font-size : 12px;
			}
			span.paragraph {
				display: block;
				padding-left:40px;
				font-family : Monaco,Consolas,"Lucida Console",monospace;
				font-size : 12px;
				padding-top : 5px;
				padding-bottom : 10px;
			}
			span.paragraph0 {
				display: block;
				padding-left:40px;
				font-family : Monaco,Consolas,"Lucida Console",monospace;
				font-size : 12px;
				padding-top : 5px;
				padding-bottom : 0px;
			}
			a:link, a:visited {<!--fade toggle-->
			 color: #DDD;
			 text-decoration: none;
			}
			a:hover {
			 color: #E77A00;
			}
			a:link, a:visited {
			 color: #BBB;
			 text-decoration: none;
			 font-weight: bold;
			}
			a:hover {
			 font-weight: normal;
			 color: #F00;
			}
		</style>
	</head>
	<body>
	<div id="container2">
		<div id="container1">
			<div id="col1">
				<div class="label0left"> DSaS API Documentation</div>
				<p>
				The DSaS API is organized around <a target="_blank" href="http://en.wikipedia.org/wiki/Representational_State_Transfer">REST</a>. Our API is designed to have predictable, resource-oriented URLS, to use HTTP response codes to indicate API errors, and to use built-in HTTP features.  <a target="_blank" href="http://www.json.org/">JSON</a> will be returned in all responses from the API, including errors. 
				</p>
				<div>
					<h3>Service</h3>
				</div>
				<div>
					Class <span class="greyhead0right">com.datascience.gal.service.Service</span> is a web-service container, binded to the <span class="greyhead0right">http://23.21.128.180:8080/GetAnotherLabel/rest</span> url, could be runned locally on
					<span class="greyhead0right">[SERVER HOST:PORT]/GetAnotherLabel/rest</span>. 
					<p>Be aware that url is not correct till the right web-method [with parameter[s]] `s[`ve] been added, such as (please be familiar with <i>Summary of Resource URL Patterns</i> section) <span class="greyhead0right">reset, exists, loadCategories e.g.</span></p>
				</div>
				<div>
					<h3>Brief Intro</h3>
				</div>
				<div>
					To setup locally the project you may need Maven (3.x.x) (cmd mvn --version),
					Tomcat 6, JRE/JDK(1.6, 6.x.0_x), MySQL. 
					<p>MSQL is needed ONLY if you are suppose to use Web-Service named Service,
					for other purposes of using DSaS you may be interested in reading <a href="https://github.com/ipeirotis/Get-Another-Label/wiki/How-to-run" target="_blank">How to run DSaS</a>
					</p>
				</div>
				<div>
					<h3>What for do we need database? Which Pitfalls could we be faced with?</h3>
				</div>
				<div>
					<p>
					We need database to save to/load from by Id any data structure, such as 
					<span class="greyhead0right">BatchDawidSkene/IncrementalDawidSkene</span>, <span class="greyhead0right">Category</span>, various collections e.g.
					</p>
					<p>
					Every web-method which contain <span class="greyhead0right">id</span> parameter always refers to the specific record of the table in database. Yes, right, ONE table, which gives a hand every entity, of any type, any time. Web-methods are NOT syncronized at the current stage of development. Table COULD contain duplicate data.
					</p>
				</div>
				<div>
					<h3>Which type of data is expected? How to test? In which order? Is it critical to keep some ordering of calling?</h3>
				</div>
				<div>
					<div>Usually <span class="greyhead0right">id</span> may not be only numeric, you may try any characters</div>
					<div>Data should has JSON type, to generate <span class="greyhead0right">Category</span> or <span class="greyhead0right">Collection of categories</span> you may use
					<a target="_blank" href="http://23.21.128.180:8080/GetAnotherLabel/rest/compose/composeDSForm">Composer</a> Also you may try <a target="_blank" href="http://curl.haxx.se/download.html">cURL</a>.
					</div>
				</div>
				<div>
					<p>
					Web-methods are splitted by two groups, POST and GET oriented. To test GET methods you may just type in browser specific url, to test POST web-methods you may create simple html file with form.
					</p>
				</div>
				<div>
					<p>
					Order of testing is important because you need to be sure that data which you treat exists.
					</p>
				</div>
				<div>
					Order of calling depends of previous executions. For instance every method name of which starts from
					<span class="greyhead0right">load</span> (<span class="greyhead0right">loadCategories</span>, <span class="greyhead0right">loadGoldLabels</span>) stores/updates object <span class="greyhead0right">BatchDawidSkene/IncrementalDawidSkene</span> to/into the database. Afterwards any other web-method may use that object by specific <span class="greyhead0right">id</span> which you may keep in mind. None of the methods return list of objects, JFYI. 
				</div>
			</div>
			<div id="col2">
				<div style="margin-top:50px;"></div>
				<div class="label1right">
					Libraries
				</div>
				<p>
					Library <a target="_blank" href="https://github.com/jattenberg/DSaS">available here</a>, need to be built as .WAR and deployed on Tomcat 6.xx
				</p>
				<div class="label1right">
					API Endpoint
				</div>
				<p>
					<a target="_blank" href="http://23.21.128.180:8080/GetAnotherLabel/rest/">available here</a>, just add web-method name and parameters
				</p>
				<div class="label1right">
					Summary of Resource URL Patterns
				</div>
				<div class="textright">
					<span class="greyhead0right">GET </span><a id="more_ping" href="" onclick="return false;">ping</a>
					<p id="more_ping" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">a simple method to see if the service is awake</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">none</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">http://23.21.128.180:8080/GetAnotherLabel/rest/ping</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">"processing request at: 2012-03-27T22:06:06.619Z"</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">GET </span><a id="more_reset" href="" onclick="return false;">reset?id={id}</a><p id="more_reset" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">resets the ds model</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">http://23.21.128.180:8080/GetAnotherLabel/rest/reset?id=1</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">nullified the ds object, and deleted ds with id 1</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">GET </span><a id="more_exists" href="" onclick="return false;">exists?id={id}</a>
					<p id="more_exists" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">checks if BatchDawidSkene/IncrementalDawidSkene object with specified identefier exists in database (and cache)</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">http://23.21.128.180:8080/GetAnotherLabel/rest/exists?id=7</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">true</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">POST </span><a id="more_loadCategories" href="" onclick="return false;">loadCategories?categories={categories}&id={id}&incremental={incremental}</a>
					<p id="more_loadCategories" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">load Categories and saves BatchDawidSkene/IncrementalDawidSkene object with specified identefier exists to the database (and cache)</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[JSON Array]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[number]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">[{"name":"name1","prior":-1.0,"misclassification_cost":{}},{"name":"name2","prior":-1.0,"misclassification_cost":{}}]</span>
						<span class="paragraph">2</span>
						<span class="paragraph">10</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">built a ds with 2 categories[{"name":"name1","prior":0.0,"misclassification_cost":{"name1":0.0,"name2":1.0}},{"name":"name2","prior":0.0,"misclassification_cost":{"name1":1.0,"name2":0.0}}]</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">POST </span><a id="more_loadCosts" href="" onclick="return false;">loadCosts?loadCosts={loadCosts}&id={id}</a>
					<p id="more_loadCosts" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">Loads a json set of misclassification cost objects</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[JSON Array]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">[{"categoryFrom":"name1","categoryTo":"name2","cost":0.9},{"categoryFrom":"name1","categoryTo":"name2","cost":0.1},{"categoryFrom":"name2","categoryTo":"name1","cost":0.4}]</span>
						<span class="paragraph">2</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">adding 3 new misclassification costs</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">POST </span><a id="more_loadWorkerAssignedLabel" href="" onclick="return false;">loadWorkerAssignedLabel?data={data}&id={id}</a>
					<p id="more_loadWorkerAssignedLabel" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">add a worker-assigned label to the model</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[JSON Object]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">{"workerName":"workerName","objectName":"objectName","categoryName":"name1"}</span>
						<span class="paragraph">2</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">adding {"workerName":"workerName","objectName":"objectName","categoryName":"name1"}</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">POST </span><a id="more_loadWorkerAssignedLabels" href="" onclick="return false;">loadWorkerAssignedLabels?data={data}&id={id}</a>
					<p id="more_loadWorkerAssignedLabels" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">add a worker-assigned label to the model</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[JSON Array]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">[{"workerName":"workerName","objectName":"objectName","categoryName":"name1"},{"workerName":"workerName2","objectName":"objectName2","categoryName":"name2"}]</span>
						<span class="paragraph">2</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">adding 2 labels</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">POST </span><a id="more_loadGoldLabel" href="" onclick="return false;">loadGoldLabel?data={data}&id={id}</a>
					<p id="more_loadGoldLabel" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">add a gold label to the model</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[JSON Object]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">{"objectName":"objectName2","correctCategory":"name1"}</span>
						<span class="paragraph">2</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">adding gold label: {"objectName":"objectName2","correctCategory":"name1"}</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">POST </span><a id="more_loadGoldLabels" href="" onclick="return false;">loadGoldLabels?data={data}&id={id}</a>
					<p id="more_loadGoldLabels" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">add gold labels to the model</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[JSON Array]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">[{"objectName":"objectName2","correctCategory":"name1"}, {"objectName":"objectName","correctCategory":"name2"}]</span>
						<span class="paragraph">2</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">adding 2 gold labels</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">GET </span><a id="more_majorityVote" href="" onclick="return false;">majorityVote?id={id}&objectName={objectName}</a>
					<p id="more_majorityVote" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">computes majority votes for object</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">2</span>
						<span class="paragraph">objectName2</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">"name1"</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">POST </span><a id="more_majorityVotes1" href="" onclick="return false;">majorityVotes?id={id}&objects={objects}</a>
					<p id="more_majorityVotes1" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">computes majority votes for objects</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[JSON Array]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">2</span>
						<span class="paragraph">[objectName,objectName2]</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">{"objectName":"name2","objectName2":"name1"}</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">GET </span><a id="more_majorityVotes2" href="" onclick="return false;">majorityVotes?id={id}</a>
					<p id="more_majorityVotes2" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">computes majority votes for objects</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">2</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">{"objectName":"name2","objectName2":"name1"}</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">POST </span><a id="more_objectProbs1" href="" onclick="return false;">objectProbs?id={id}&objects={objects}</a>
					<p id="more_objectProbs1" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">computes class probs objects</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[JSON Array]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">2</span>
						<span class="paragraph">[objectName,objectName2]</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">{"objectName2":{"name1":1.0,"name2":0.0},"objectName":{"name1":0.0,"name2":1.0}}</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">GET </span><a id="more_objectProb" href="" onclick="return false;">objectProb?id={id}&object={object}</a>
					<p id="more_objectProb" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">computes class probs objects</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[JSON Object]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">2</span>
						<span class="paragraph">ObjectName2</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">{"name1":1.0,"name2":0.0}</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">GET </span><a id="more_computeBlocking" href="" onclick="return false;">computeBlocking?iterations={iterations}&id={id}</a>
					<p id="more_computeBlocking" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">runs the algorithm, iterating the specified number of times </span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[number]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">2</span>
						<span class="paragraph">10</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">performed ds iteration 10 times, took: 1ms.</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">GET </span><a id="more_printWorkerSummary" href="" onclick="return false;">printWorkerSummary?verbose={verbose}&id={id}</a>
					<p id="more_printWorkerSummary" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">print Worker`s info</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">no matter</span>
						<span class="paragraph">2</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph0">Worker: workerName</span>
						<span class="paragraph0">Error Rate: 46.0%</span>
						<span class="paragraph0">Quality (Expected): 41%</span>
						<span class="paragraph0">Quality (Optimized): 5%</span>
						<span class="paragraph0">Number of Annotations: 1</span>
						<span class="paragraph0">Number of Gold Tests: 1</span>
						<span class="paragraph0">Confusion Matrix: </span>
						<span class="paragraph0">P[name1->name1]=90.0%</span>
						<span class="paragraph0">P[name1->name2]=10.0%</span>
						<span class="paragraph0">P[name2->name1]=55.0%</span>
						<span class="paragraph0">P[name2->name2]=45.0%</span>
						<span class="paragraph0"><br/></span>
						<span class="paragraph0">Worker: workerName2</span>
						<span class="paragraph0">Error Rate: 19.0%</span>
						<span class="paragraph0">Quality (Expected): 42%</span>
						<span class="paragraph0">Quality (Optimized): 0%</span>
						<span class="paragraph0">Number of Annotations: 1</span>
						<span class="paragraph0">Number of Gold Tests: 1</span>
						<span class="paragraph0">Confusion Matrix: </span>
						<span class="paragraph0">P[name1->name1]=45.0%	P[name1->name2]=55.0%</span>
						<span class="paragraph0">P[name2->name1]=10.0%	P[name2->name2]=90.0%</span>
							
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">GET </span><a id="more_printObjectsProbs" href="" onclick="return false;">printObjectsProbs?entropy={entropy}&id={id}</a>
					<p id="more_printObjectsProbs" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">calculates and prints object`s probabilities</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[number with floating point]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">2</span>
						<span class="paragraph">0.5</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">Object	Pr[name1]	Pr[name2]	Pre-DS Majority Label	Pre-DS Min Cost Label	Post-DS Majority Label	Post-DS Min Cost Label</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">GET </span><a id="more_objectProbs2" href="" onclick="return false;">objectProbs?object={object}&entropy={entropy}&id={id}</a>
					<p id="more_objectProbs2" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">calculates and prints object`s probabilities</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[number with floating point]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">ObjectName2</span>
						<span class="paragraph">2</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">null</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">GET </span><a id="more_printPriors" href="" onclick="return false;">printPriors?id={id}</a>
					<p id="more_printPriors" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">prints priors by given id</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">2</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph0">Prior[name1]=0.5</span>
						<span class="paragraph0">Prior[name2]=0.5</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">GET </span><a id="more_classPriors" href="" onclick="return false;">classPriors?id={id}</a>
					<p id="more_classPriors" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">prints the list of the category/priority relations</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">2</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">{"name1":0.5,"name2":0.5}</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">GET </span><a id="more_getDawidSkene" href="" onclick="return false;">getDawidSkene?id={id}</a>
					<p id="more_getDawidSkene" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">prints BatchDawidSkene or IncrementalDawidSkene by given identifier</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">2</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">{"dsmethod":"UPDATEWORKERS","priorDenominator":2.0,"objects":{"objectName":{"name":"objectName","isGold":true,"correctCategory":"name2","categoryProbability":{"name1":0.0,"name2":1.0},"labels":[{"workerName":"workerName","objectName":"objectName","categoryName":"name1"}]},"objectName2":{"name":"objectName2","isGold":true,"correctCategory":"name1","categoryProbability":{"name1":1.0,"name2":0.0},"labels":[{"workerName":"workerName2","objectName":"objectName2","categoryName":"name2"}]}},"workers":{"workerName":{"name":"workerName","cm":{"categories":["name1","name2"],"matrix":{"{\"to\":\"name1\",\"from\":\"name2\"}":1.1,"{\"to\":\"name2\",\"from\":\"name1\"}":0.1,"{\"to\":\"name2\",\"from\":\"name2\"}":0.9,"{\"to\":\"name1\",\"from\":\"name1\"}":0.9},"rowDenominator":{"name1":0.9999999999999999,"name2":2.0}},"labels":[{"workerName":"workerName","objectName":"objectName","categoryName":"name1"}]},"workerName2":{"name":"workerName2","cm":{"categories":["name1","name2"],"matrix":{"{\"to\":\"name1\",\"from\":\"name2\"}":0.1,"{\"to\":\"name2\",\"from\":\"name1\"}":1.1,"{\"to\":\"name2\",\"from\":\"name2\"}":0.8999999999999999,"{\"to\":\"name1\",\"from\":\"name1\"}":0.9},"rowDenominator":{"name1":2.0,"name2":1.0}},"labels":[{"workerName":"workerName2","objectName":"objectName2","categoryName":"name2"}]}},"categories":{"name1":{"name":"name1","prior":1.0,"misclassification_cost":{"name1":0.0,"name2":0.1}},"name2":{"name":"name2","prior":1.0,"misclassification_cost":{"name1":0.4,"name2":0.0}}},"fixedPriors":false,"id":"2"}</span>
					</p>
				</div>
				
				<div class="greyhead1right">EXAMPLE REQUEST</div>
				<div>
		<!--<form method="post" action="http://23.21.128.180:8080/GetAnotherLabel/rest/loadCategories" name="formOne" enctype="application/json; charset=utf-8">
			<input name="id" value="2"/><input name="incremental" value="10"/>
			<textarea name="categories">[{"name":"name1","prior":-1.0,"misclassification_cost":{}},{"name":"name2","prior":-1.0,"misclassification_cost":{}}]</textarea>
			<button type="submit">Compute</button>
		</form>-->
		
		<!-- http://accessify.com/tools-and-wizards/developer-tools/quick-escape/ -->
		&lt;form method=&quot;post&quot; action=&quot;http://23.21.128.180:8080/GetAnotherLabel/rest/loadCategories&quot; name=&quot;formOne&quot; enctype=&quot;application/json; charset=utf-8&quot;&gt;
			&lt;input name=&quot;id&quot; value=&quot;2&quot;/&gt;&lt;input name=&quot;incremental&quot; value=&quot;10&quot;/&gt;
			&lt;textarea name=&quot;categories&quot;&gt;[{&quot;name&quot;:&quot;name1&quot;,&quot;prior&quot;:-1.0,&quot;misclassification_cost&quot;:{}},{&quot;name&quot;:&quot;name2&quot;,&quot;prior&quot;:-1.0,&quot;misclassification_cost&quot;:{}}]&lt;/textarea&gt;
			&lt;button type=&quot;submit&quot;&gt;Compute&lt;/button&gt;
		&lt;/form&gt;
				</div>
				<p/><p/><p/>
				<div style="margin-top:50px;"></div>
				<div class="label1right">
					Summary of Resource URL Patterns (wrapped methods WITHOUT any JSON parameters)
				</div>
				<div class="textright">
					<span class="greyhead0right">POST </span><a id="more_loadCategories_notjson" href="" onclick="return false;">loadCategories?names={category_name1,category_name2...category_nameN}&id={id}&incremental={incremental}</a>
					<p id="more_loadCategories_notjson" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">load Categories and saves BatchDawidSkene/IncrementalDawidSkene object with specified identefier exists to the database (and cache)</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[number]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">http://23.21.128.180:8080/GetAnotherLabel/rest/notjson/loadCategories</span>
						<span class="paragraph">name1, name2, name3</span>
						<span class="paragraph">12</span>
						<span class="paragraph">10</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">POST http://23.21.128.180:8080/GetAnotherLabel/rest/loadCategories returned a response status of 200 OK</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">POST </span><a id="more_loadCosts_notjson" href="" onclick="return false;">loadCosts?from={from1,from2...fromN}&to={to1,to2...toN}&cost={cost1,cost2..costN}&id={id}</a>
					<p id="more_loadCosts_notjson" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">Loads a json set of misclassification cost objects</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">http://23.21.128.180:8080/GetAnotherLabel/rest/notjson/loadCosts</span>
						<span class="paragraph">name1,name1,name2</span>
						<span class="paragraph">name2,name2,name1</span>
						<span class="paragraph">0.9,0.1,0.4</span>
						<span class="paragraph">2</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">POST http://23.21.128.180:8080/GetAnotherLabel/rest/loadCosts returned a response status of 200 OK</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">POST </span><a id="more_loadWorkerAssignedLabel_notjson" href="" onclick="return false;">loadWorkerAssignedLabel?workerName={workerName}&objectName={objectName}&categoryName={categoryName}&id={id}</a>
					<p id="more_loadWorkerAssignedLabel_notjson" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">add a worker-assigned label to the model</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">http://23.21.128.180:8080/GetAnotherLabel/rest/notjson/loadWorkerAssignedLabels</span>
						<span class="paragraph">workerName11</span>
						<span class="paragraph">objectName22</span>
						<span class="paragraph">name1</span>
						<span class="paragraph">2</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">POST http://23.21.128.180:8080/GetAnotherLabel/rest/loadWorkerAssignedLabel returned a response status of 200 OK</span>					
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">POST </span><a id="more_loadWorkerAssignedLabels_notjson" href="" onclick="return false;">loadWorkerAssignedLabels?workerName={workerName1,workerName2..workerNameN}&objectName={objectName1,objectName2..objectNameN}&categoryName={categoryName1,categoryName2..categoryNameN}&id={id}</a>
					<p id="more_loadWorkerAssignedLabels_notjson" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">add a worker-assigned labels to the model</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">http://23.21.128.180:8080/GetAnotherLabel/rest/notjson/loadWorkerAssignedLabels</span>
						<span class="paragraph">workerName11,workerName12</span>
						<span class="paragraph">objectName22,objectName23</span>
						<span class="paragraph">name1,name2</span>
						<span class="paragraph">2</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">POST http://23.21.128.180:8080/GetAnotherLabel/rest/loadWorkerAssignedLabels returned a response status of 200 OK</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">POST </span><a id="more_loadGoldLabel_notjson" href="" onclick="return false;">loadGoldLabel?objectName={objectName}&correctCategory={correctCategory}&id={id}</a>
					<p id="more_loadGoldLabel_notjson" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">add a gold label to the model</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">http://23.21.128.180:8080/GetAnotherLabel/rest/notjson/loadGoldLabel</span>
						<span class="paragraph">objectName2</span>
						<span class="paragraph">name1</span>
						<span class="paragraph">2</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">POST http://23.21.128.180:8080/GetAnotherLabel/rest/loadGoldLabel returned a response status of 200 OK</span>
					</p>
				</div>
				<div class="textright">
					<span class="greyhead0right">POST </span><a id="more_loadGoldLabels_notjson" href="" onclick="return false;">loadGoldLabels?objectName={objectName1,objectName2..objectNameN}&correctCategory={correctCategory1,correctCategory2..correctCategoryN}&id={id}</a>
					<p id="more_loadGoldLabels_notjson" style="display: none;">
						<span class="greyhead2right">DESCRIPTION</span>
						<span class="paragraph">add gold labels to the model</span>
						<span class="greyhead2right">PARAMETERS</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="paragraph">[any sequence of characters]</span>
						<span class="greyhead2right">SAMPLE REQUEST</span>
						<span class="paragraph">http://23.21.128.180:8080/GetAnotherLabel/rest/notjson/loadGoldLabels</span>
						<span class="paragraph">objectName2, on3</span>
						<span class="paragraph">name1,name2</span>
						<span class="paragraph">2</span>
						<span class="greyhead2right">SAMPLE RESPONSE</span>
						<span class="paragraph">POST http://23.21.128.180:8080/GetAnotherLabel/rest/loadGoldLabels returned a response status of 200 OK</span>
					</p>
				</div>
			</div>
		</div>
	</div>
		<script>
			$("a#more_ping").click(function () {
			  $("p#more_ping").fadeToggle("slow", function () {});
			});
			$("a#more_reset").click(function () {
			  $("p#more_reset").fadeToggle("slow", function () {});
			});
			$("a#more_exists").click(function () {
			  $("p#more_exists").fadeToggle("slow", function () {});
			});
			$("a#more_loadCategories").click(function () {
			  $("p#more_loadCategories").fadeToggle("slow", function () {});
			});
			$("a#more_loadCosts").click(function () {
			  $("p#more_loadCosts").fadeToggle("slow", function () {});
			});
			$("a#more_loadWorkerAssignedLabel").click(function () {
			  $("p#more_loadWorkerAssignedLabel").fadeToggle("slow", function () {});
			});
			$("a#more_loadWorkerAssignedLabels").click(function () {
			  $("p#more_loadWorkerAssignedLabels").fadeToggle("slow", function () {});
			});
			$("a#more_loadGoldLabel").click(function () {
			  $("p#more_loadGoldLabel").fadeToggle("slow", function () {});
			});
			$("a#more_loadGoldLabels").click(function () {
			  $("p#more_loadGoldLabels").fadeToggle("slow", function () {});
			});
			$("a#more_majorityVote").click(function () {
			  $("p#more_majorityVote").fadeToggle("slow", function () {});
			});
			$("a#more_majorityVotes1").click(function () {
			  $("p#more_majorityVotes1").fadeToggle("slow", function () {});
			});
			$("a#more_majorityVotes2").click(function () {
			  $("p#more_majorityVotes2").fadeToggle("slow", function () {});
			});
			$("a#more_objectProbs1").click(function () {
			  $("p#more_objectProbs1").fadeToggle("slow", function () {});
			});
			$("a#more_objectProb").click(function () {
			  $("p#more_objectProb").fadeToggle("slow", function () {});
			});
			$("a#more_computeBlocking").click(function () {
			  $("p#more_computeBlocking").fadeToggle("slow", function () {});
			});
			$("a#more_printWorkerSummary").click(function () {
			  $("p#more_printWorkerSummary").fadeToggle("slow", function () {});
			});
			$("a#more_printObjectsProbs").click(function () {
			  $("p#more_printObjectsProbs").fadeToggle("slow", function () {});
			});
			$("a#more_objectProbs2").click(function () {
			  $("p#more_objectProbs2").fadeToggle("slow", function () {});
			});
			$("a#more_printPriors").click(function () {
			  $("p#more_printPriors").fadeToggle("slow", function () {});
			});
			$("a#more_classPriors").click(function () {
			  $("p#more_classPriors").fadeToggle("slow", function () {});
			});
			$("a#more_getDawidSkene").click(function () {
			  $("p#more_getDawidSkene").fadeToggle("slow", function () {});
			});
			$("a#more_loadCategories_notjson").click(function () {
				  $("p#more_loadCategories_notjson").fadeToggle("slow", function () {});
			});
			$("a#more_loadCosts_notjson").click(function () {
				  $("p#more_loadCosts_notjson").fadeToggle("slow", function () {});
			});
			$("a#more_loadGoldLabel_notjson").click(function () {
				  $("p#more_loadGoldLabel_notjson").fadeToggle("slow", function () {});
			});
			$("a#more_loadGoldLabels_notjson").click(function () {
				  $("p#more_loadGoldLabels_notjson").fadeToggle("slow", function () {});
			});
			$("a#more_loadWorkerAssignedLabel_notjson").click(function () {
				  $("p#more_loadWorkerAssignedLabel_notjson").fadeToggle("slow", function () {});
			});
			$("a#more_loadWorkerAssignedLabels_notjson").click(function () {
				  $("p#more_loadWorkerAssignedLabels_notjson").fadeToggle("slow", function () {});
			});
		</script>
	</body>
</html>