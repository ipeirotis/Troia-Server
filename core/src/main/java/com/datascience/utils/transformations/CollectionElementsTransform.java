package com.datascience.utils.transformations;

import com.datascience.utils.ITransformation;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @Author: konrad
 */
public class CollectionElementsTransform<A, B> implements ITransformation<Collection<A>, Collection<B>> {

	ITransformation<A, B> transformation;

	public CollectionElementsTransform(ITransformation<A, B> transformation){
		this.transformation = transformation;
	}

	@Override
	public Collection<B> transform(Collection<A> object) {
		List<B> list = new LinkedList<B>();
		for (A el: object) {
			list.add(transformation.transform(el));
		}
		return list;
	}

	@Override
	public Collection<A> inverse(Collection<B> object) {
		List<A> list = new LinkedList<A>();
		for (B el: object) {
			list.add(transformation.inverse(el));
		}
		return list;	}
}
