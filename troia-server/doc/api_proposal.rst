Troia API proposal
==================

Current version of REST API is very cluttered and not structured well.
Here we propose version that will hopefully turn out to be better and easier to extend in future.

Let URL stand for entry point.

We should use API versioning.
This is quite common practice used by most successful Internet companies like Google or Yahoo



Entry: URL/api/v1/

We need to enable some authorization - without that this service is useless for many companies


Methods
-------

::

  GET /ping <- should return system status: its time and whether it can connect to DB/storage


We assume that you are authorized when you make following requests.

::

    GET /jobs/ <- lists all your jobs
    GET /jobs/id:/ <- returns base info about job - when it was created etc and links to more data?

    GET /jobs/id:/status/

    GET /jobs/id:/costMatrix

    GET /jobs/id:/labels/ <- list all labels
    GET /jobs/id:/labels/label_id:/ <- some stats about this label like how much votes use it?

    GET /jobs/id:/workers/ <- list all workers that voted in this job
    GET /jobs/id:/workers/worker_id:/ <- some stats about worker

    GET /jobs/id:/datums/ <- list all items for which we have votes (all items are included: gold, validation etc)
    GET /jobs/id:/datums/datum_id:/ <- all assigns that we were given to this object and possibly algorithms decision about label for this item?

    GET /jobs/id:/goldDatums/ <- similar to datums
    GET /jobs/id:/goldDatums/datum_id:/ <- similar to datums
    GET /jobs/id:/evaluationDatums/ <- similar to datums
    GET /jobs/id:/evaluationDatums/datum_id:/ <- similar to datums

    GET /jobs/id:/assignedLabels/ <- list all workers assigns

    GET /jobs/id:/prediction/algorithm:/datums/ <- lists all object with their predicted labels
    GET /jobs/id:/prediction/algorithm:/datums/datum_id:/ <- return more detailed info like labels probability distribution etc. Can be specific to given algorithm
    GET /jobs/id:/prediction/algorithm:/datums/datum_id:/estimatedCost <- calculates estimated cost, takes method as argument

    GET /jobs/id:/prediction/algorithm:/workers/worker_id:/ <- returns worker quality related data


    POST /jobs/ <- create job with some random id and return this id similar to next one
    POST /jobs/id:/ <- create new job with given id and with specified parameters
    POST /jobs/id:/reset

    POST /jobs/id:/costMatrix <- sends cost matrix
    POST /jobs/id:/votes/ <- add votes to system - we have only version that adds multiple votes - possibly only one

    POST /jobs/id:/prediction/algorithm:/calculate <- starts calculation for this project, takes parameters specific to used algorithm like number of iterations etc
    POST /jobs/id:/prediction/algorithm:/


    DELETE /jobs/id:/ <- deletes job

NOTE: Do we want to be able to remove some votes?



Items and their JSON representations
------------------------------------

- job - represented with its ID - String
- item - aka object - represented with its String id. In dict represented with: "item_id": IID
- worker - represented with its String id. In dict represented with: "worker_id": WID
- goldItem/validationItem - dict in form:

    {"item_id": ABC, "trueValue": x}

- costMatrix - json list of dictionaries of form:

    {"trueValue": x, "predictedValue": y, "errorCost": z}

