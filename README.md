Troia-Server
============

Before setting up Troia you have to configure environment on target computer
so it can prepare and run this service. You have to download the following software:

* java - http://www.oracle.com/technetwork/java/javase/downloads/index.html
* tomcat - http://tomcat.apache.org/


<a href="http://project-troia.com/media/downloads/troia.war">
  Download precompiled WAR file
</a>

If you want to compile Troia from source you will need:

* git - http://git-scm.com
* maven - http://maven.apache.org/

Note that if you have package manager, like Synaptic, you can simply download all this software with it.
If you are going to use package manager remember to download *tomcat6-admin* package as well.
As for servlet container you can use different one but this tutorial will be written with Tomcat in mind.

Setting up Tomcat
-----------------

You can find detailed documentation on installing tomcat [here](http://tomcat.apache.org/tomcat-6.0-doc/setup.html).

In order to be able to use Tomcat's administrative functions, you must create an user with administrator privileges.
You can do that by modifying *tomcat-users.xml* file.

* if you are using Linux, the file is probably located at */etc/tomcat6/tomcat-users.xml* path
* on Windows, the file can be found at the location *<CATALINA_HOME>/conf/tomcat-users.xml*

To create an user with full required privileges you must add the following line into this file

    <user username="administrator" password="drowssap" roles="admin,manager-gui" />

This will create user *administrator* with password *drowssap* and all privileges needed for this project.

Deploying WAR
-------------

To deploy the .war file, open Tomcat web application manager. If you are setting up Troia on the localhost and
Tomcat runs on port 8080, web application manager will be up at the following address:

    http://localhost:8080/manager/html

Find the part of the manager that allows you to upload and deploy .war files from disk.
After that you should simply choose .war that was generated in previous step and deploy it.
If the deployment succeeds, you will see Troia listed under Applications panel and its status will be 'Running'. If you'll the visit following url:

    http://localhost:8080/troia/status

As a result you should get a JSON response saying that server status is OK and it's job storage is **CachedWithRegularDumpMEMORY_FULLUsingExecutor**.

That's all - now you can start playing with troia server. But it is worth mentioning that after restarting tomcat all data you've provided to troia will be lost (all data is stored in memory).
To add persistance you also need to download:

* mysql - http://www.mysql.com/

and configure your server to use database.

Configuring server
------------------

Troia server should work out of the box (using in memory jobs storage). But there are some parameters you might find useful. To change them go to the following url:

    http://localhost:8080/troia/config

Parameters meaning:

* CACHE_DUMP_TIME - for how long (in seconds) job should be stored in the cache
* CACHE_SIZE - how many jobs should be stored in the cache
* DOWNLOADS_PATH - the path for downloading the zip archives containing the computation results
* EXECUTOR_THREADS_NUM - number of executor threads
* RESPONSES_CACHE_SIZE - how many responses should be store in the cache
* RESPONSES_DUMP_TIME - for how long (in seconds) responses should be stored in the cache
* JOBS_STORAGE - you can select from: MEMORY_FULL, MEMORY_KV, DB_FULL, DB_KV_MEMCACHE_SIMPLE or DB_KV_SIMPLE

DB parameters (used only if JOBS_STORAGE is set to DB_something:

* DB_DRIVER_CLASS - using mysql is recommended but not required
* DB_NAME - the database name
* DB_PASSWORD - mysql user's password
* DB_URL - the url of mysql host machine
* DB_USER - mysql user who owns the database

Memcache parameters (used only if JOBS_STORAGE is set to DB_KV_MEMCACHE_something):

* MEMCACHE_EXPIRATION_TIME - for how long (in seconds) values should be stored in the memcache
* MEMCACHE_PORT - memcache service port
* MEMCACHE_URL - memcache service url

Freezing settings:

There is also checkbox *settings freezed?*. Once freezed settings can't be changed (in order to change them you need to redeploy the WAR).
That's for security reasons to prevent others from changing our settings.
If settings are freezed we also can't see confidental data like database user and password.
There is one more parameter **FREEZE_CONFIGURATION_AT_START** which might be useful when you building your own war file.
After setting it to *true* in troia.properties file and building war file, after each deploy troia settings will be freezed by default.



After filling the form, click Submit button.
If you've changed job storage to use database you should now see *recreate (drop and create) database* button below Submit one.
By clicking it you create all needed tables depending on selected data model (FULL/KV).
To check if everything is working correctly you can punch the following into the browser's url bar:

    http://localhost:8080/troia/status

As a result you should get a JSON response saying that everything is OK.


Building WAR file
-----------------

To build WAR file you should enter project main directory execute following command

    mvn package


As a result *target* directory will appear in service directory.
It should contain file **service.war**.


