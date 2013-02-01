GALC API proposition
====================

We will separate jobs that will work on continuous data by using different entry:

::

    /cjobs/{job_id}

Later this can be changed.

All requests use "redirect" for their responses so that we could be asynchronous.
Redirect will be located:

::

    /responses/{response_id}/cjobs/{job_id}/...


Job creation/deletion is similar to previous solution.

::

    GET /cjobs/{job_id}/objects -> list all objects
    GET /cjobs/{job_id}/objects/{object_id} -> base info about given object
    GET /cjobs/{job_id}/goldObjects -> list all gold objects
    GET /cjobs/{job_id}/objects/{object_id}/assignedLabels
    GET /cjobs/{job_id}/assigns/ -> list all assigns
    GET /cjobs/{job_id}/workers/ -> list all workers
    GET /cjobs/{job_id}/workers/{worker_id} -> base info about given worker
    GET /cjobs/{job_id}/workers/{worker_id}/assigns

For data upload:

::

    POST /cjobs/{job_id}/objects/{new_object_id} -> creates new object
    POST /cjobs/{job_id}/goldObjects/{object_id} params: "label": value
    POST /cjobs/{job_id}/assigns/ params: "worker": worker_id, "object": object_id, "label": value

Starting calculations:

::

    POST /cjobs/{job_id}/calculate

Getting results:

::

    GET /cjobs/{job_id}/prediction/objects -> list all objects with predicted values
    GET /cjobs/{job_id}/prediction/workers -> list worker quality related data

