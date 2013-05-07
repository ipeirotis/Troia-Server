package com.datascience.scheduler;

import java.util.Iterator;

/**
 * @Author: konrad
 */
public interface IIterablePriorityQueue<T> {

	void clear();
	void addReplacing(T object, double priority);
	void remove(T object);
	T first();
	T getAndRemoveFirst();

	Iterator<T> iterator();
}
