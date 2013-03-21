package com.datascience.core.algorithms;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;


/**
 * @Author: konrad
 */
public interface INewDataObserver<T> {

	void newAssign(AssignedLabel<T> assign);
	void newGoldObject(LObject<T> object);

	// Those two might be important for scheduling system
	void newObject(LObject<T> object);
	void newWorker(Worker<T> worker);
}
