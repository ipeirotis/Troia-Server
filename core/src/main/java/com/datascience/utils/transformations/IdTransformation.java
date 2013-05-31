package com.datascience.utils.transformations;

import com.datascience.utils.ITransformation;

public class IdTransformation<A> implements ITransformation<A, A> {
	@Override
	public A transform(A object) {
		return object;
	}

	@Override
	public A inverse(A object) {
		return object;
	}
}
