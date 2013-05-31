package com.datascience.utils.transformations;

import com.datascience.utils.ITransformation;

public class CastingTransform<A> implements ITransformation<A, Object>{

	@Override
	public Object transform(A object) {
		return object;
	}

	@Override
	public A inverse(Object object) {
		return (A) object;
	}
}