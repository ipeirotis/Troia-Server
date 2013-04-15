package com.datascience.core.results;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;

/**
 * @Author: konrad
 */
public interface INewResultsListener<T, U, V> {

	void newResultsForAll();

	void newResultsForObject(LObject<T> object, U results);

	void newResultsForWorker(Worker<T> worker, V results);
}
