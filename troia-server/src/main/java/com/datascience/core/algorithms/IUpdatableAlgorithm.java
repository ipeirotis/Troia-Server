package com.datascience.core.algorithms;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;

import java.util.Collection;

/**
 * @Author: konrad
 */
public interface IUpdatableAlgorithm<T> {

	void newAssigns(Collection<AssignedLabel<T>> assigns);
	void newGoldObjects(Collection<LObject<T>> objects);

	// Those two might be important for scheduling system
	void newObjects(Collection<LObject<T>> objects);
	void newWorkers(Collection<Worker<T>> workers);
}
