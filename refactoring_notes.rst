Notes on refactor
=================

Job creation
------------

  - need to specify algorithm + it's parameters (default number of iterations, epsilon in loglikehood etc.)
  - whether would like to use scheduler (default: False) and what kind - number of assigns or based on costs


Database
--------

Separate columns for:

  - id
  - type/kind (nominal/continuous)
  - timestamp of last modification
  - algorithm (+ parameters like loglikehood epsilon, default number of iterations)
  - inputData (Data<T>)
  - results
  - scheduler - informations about configuration (like what priority calculator was used)


Python clinet
-------------

Needs update, possibly also refactor due to lack of common parts between nominal and continuous

