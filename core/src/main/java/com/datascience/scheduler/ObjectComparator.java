package com.datascience.scheduler;

import com.datascience.core.base.LObject;

import java.util.Comparator;

public class ObjectComparator<T> implements Comparator<LObject<T>> {

	private IPriorityCalculator<T> calculator;

	public ObjectComparator(IPriorityCalculator<T> calculator) {
		this.calculator = calculator;
	}

	@Override
	public int compare(LObject<T> lObject1, LObject<T> lObject2) {
		return ((Double) calculator.getPriority(lObject1)).compareTo(calculator.getPriority(lObject2));
	}
}
