package com.datascience.utils.transformations;

import com.datascience.utils.ITransformation;

/**
 * @Author: konrad
 */
public class ComposingTransform<A, B, C> implements ITransformation<A, C> {

	protected ITransformation<A, B> fstTransform;
	protected ITransformation<B, C> sndTransform;

	public ComposingTransform(ITransformation<A, B> fstTransform, ITransformation<B, C> sndTransform){
		this.fstTransform = fstTransform;
		this.sndTransform = sndTransform;
	}

	@Override
	public C transform(A object) {
		return sndTransform.transform(fstTransform.transform(object));
	}

	@Override
	public A inverse(C object) {
		return fstTransform.inverse(sndTransform.inverse(object));
	}
}
