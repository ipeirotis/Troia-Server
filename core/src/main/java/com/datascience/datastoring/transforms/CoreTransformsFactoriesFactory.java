package com.datascience.datastoring.transforms;

import com.datascience.datastoring.Constants;
import com.datascience.serialization.json.GSONSerializer;
import com.datascience.utils.transformations.thrift.ThriftCoreTransformFactory;

/**
 * @Author: konrad
 */
public class CoreTransformsFactoriesFactory {

	static public ICoreTransformsFactory create(String name){
		name = Constants.t(name);
		if (name == "JSON")
			return new SerializerBasedCoreTransformsFactory(new GSONSerializer());
		if (name == "SIMPLE")
			return new SimpleStringCoreTransformsFactory();
		if (name == "THRIFT")
			return new ThriftCoreTransformFactory();
		// TODO XXX FIXME - add protobuff, Thrift etc.
		throw new IllegalArgumentException("Unknown serialization: " + name);
	}
}
