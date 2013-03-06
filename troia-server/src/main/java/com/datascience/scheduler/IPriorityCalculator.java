package com.datascience.scheduler;

import com.datascience.core.base.LObject;

/**
 * @Author: konrad
 */
public interface IPriorityCalculator<T> {

	double getPriority(LObject<T> object);
}
