package com.datascience.serialization;

import java.lang.reflect.Type;

/**
 *
 * @author konrad
 */
public interface ISerializer {
	
	public String serialize(Object object);
	public String getMediaType();
	public <T> T parse(String input, Type type);
	public Object getRaw(Object object);
}
