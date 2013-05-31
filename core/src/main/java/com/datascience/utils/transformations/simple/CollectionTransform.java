package com.datascience.utils.transformations.simple;

import com.datascience.utils.ITransformation;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.Collection;
import java.util.LinkedList;

/**
 * @Author: konrad
 */
public class CollectionTransform<T> implements ITransformation<Collection<T>, String> {

	protected Joiner joiner;
	protected Splitter splitter;
	ITransformation<T, String> itemTransformation;

	public CollectionTransform(String separator, ITransformation<T, String> itemTransformation){
		joiner = Joiner.on(separator);
		splitter = Splitter.on(separator).omitEmptyStrings();
		this.itemTransformation = itemTransformation;
	}

	@Override
	public String transform(Collection<T> items) {
		LinkedList<String> stringItems = new LinkedList<String>();
		for (T item : items){
			stringItems.add(itemTransformation.transform(item));
		}
		return joiner.join(stringItems);
	}

	@Override
	public Collection<T> inverse(String object) {
		Collection<T> items = new LinkedList<T>();
		for (String item : splitter.split(object)){
			items.add(itemTransformation.inverse(item));
		}
		return items;
	}
}
