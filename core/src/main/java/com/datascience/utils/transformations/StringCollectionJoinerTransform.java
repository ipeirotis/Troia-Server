package com.datascience.utils.transformations;

import com.datascience.utils.ITransformation;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.Collection;

/**
 * @Author: konrad
 */
public class StringCollectionJoinerTransform implements ITransformation<Collection<String>, String> {

	protected Joiner joiner;
	protected Splitter splitter;

	public StringCollectionJoinerTransform(String separator){
		joiner = Joiner.on(separator);
		splitter = Splitter.on(separator);
	}

	@Override
	public String transform(Collection<String> object) {
		return joiner.join(object);
	}

	@Override
	public Collection<String> inverse(String object) {
		Iterable<String> trueItems = splitter.split(object);
		return Lists.newLinkedList(trueItems);
	}
}
