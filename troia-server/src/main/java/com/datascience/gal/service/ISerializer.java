package com.datascience.gal.service;

import java.lang.reflect.Type;

/**
 *
 * @author konrad
 */
public interface ISerializer {
	
	public String serialize(Object object);
	public String getMediaType();
	public <T> T parse(String input, Type type);
	
}
