package com.datascience.scheduler;

import com.datascience.core.base.LObject;
import com.google.common.collect.ComparisonChain;

import java.util.Comparator;

public class ObjectComparator<T> implements Comparator<LObject<T>> {

	private IPriorityCalculator<T> calculator;

	public ObjectComparator(IPriorityCalculator<T> calculator) {
		this.calculator = calculator;
	}

	@Override
	public int compare(LObject<T> lObject1, LObject<T> lObject2) {
		return ComparisonChain.start()
				.compare(calculator.getPriority(lObject1), calculator.getPriority(lObject2))
				.result();
	}
}
