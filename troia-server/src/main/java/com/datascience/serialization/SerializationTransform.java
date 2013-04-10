package com.datascience.serialization;

import com.datascience.utils.ITransformation;

import java.lang.reflect.Type;

/**
 * @Author: konrad
 */
public class SerializationTransform<T> implements ITransformation<T, String> {

	protected ISerializer serializer;
	protected Type expectedType;

	public SerializationTransform(ISerializer serializer, Type expectedType){
		this.serializer = serializer;
		this.expectedType = expectedType;
	}

	@Override
	public String transform(T object) {
		return serializer.serialize(object);
	}

	@Override
	public T iverse(String object) {
		return serializer.parse(object, expectedType);
	}
}
