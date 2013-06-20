package com.datascience.datastoring.adapters.kv;

import com.datascience.utils.ITransformation;

/**
 * @Author: konrad
 */
public class VTransformingKVWrapper<V1, V2> implements IKVStorage<V1> {

	ITransformation<V1, V2> transformation;
	IKVStorage<V2> wrapped;

	public VTransformingKVWrapper(IKVStorage<V2> wrapped, ITransformation<V1, V2> transformation){
		this.wrapped = wrapped;
		this.transformation = transformation;
	}

	@Override
	public void put(String key, V1 value) throws Exception {
		wrapped.put(key, transformation.transform(value));
	}

	@Override
	public V1 get(String key) throws Exception {
		V2 value = wrapped.get(key);
		if (value == null)
			return null;
		return transformation.inverse(value);
	}

	@Override
	public void remove(String key) throws Exception {
		wrapped.remove(key);
	}

	@Override
	public boolean contains(String key) throws Exception {
		return wrapped.contains(key);
	}

	@Override
	public void shutdown() throws Exception {
		wrapped.shutdown();
	}
}
