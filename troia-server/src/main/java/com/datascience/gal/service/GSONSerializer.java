package com.datascience.gal.service;

import com.datascience.core.storages.JSONUtils;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import java.lang.reflect.Type;
import javax.ws.rs.core.MediaType;

/**
 *
 * @author konrad
 */
public class GSONSerializer implements ISerializer{

	@Override
	public String serialize(Object object) {
		return JSONUtils.gson.toJson(object);
	}

	@Override
	public String getMediaType() {
		return MediaType.APPLICATION_JSON;
	}

	@Override
	public <T> T parse(String input, Type type) {
		JsonReader jr = new JsonReader(new StringReader(input));
		jr.setLenient(true);
		return JSONUtils.gson.fromJson(jr, type);
	}
}
