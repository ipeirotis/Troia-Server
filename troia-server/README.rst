In order to deploy you need to configure your MySQL database and properly set resources (user, password etc)

If you would like to handle big jobs you will need to configure MySQL for this.
In file **my.cnf** (on Ubuntu it is located in **/etc/mysql/my.cnf**) configure option **max_allowed_packet**. For example:

..

  max_allowed_packet=128MB


Final step of configuring DB is to create proper table in database.
Comands doing this are locatet in **src/main/resources/dawidprojects.sql**

GALC
----

- go to troia-server folder,
- run mvn package
- java -cp target/troia-server-1.0-jar-with-dependencies.jar com.datascience.galc.Main --correct ../data/galc/goldObjects.txt --input ../data/galc/assignedlabels.txt --output RESULTS --evalWorkers ../data/galc/evaluationWorkers.txt --evalObjects ../data/galc/evaluationObjects.txt
