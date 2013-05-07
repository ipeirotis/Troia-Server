package com.datascience.serialization.json;

import com.datascience.serialization.ISerializer;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonReader;
import java.io.StringReader;
import java.lang.reflect.Type;

/**
 *
 * @author konrad
 */
public class GSONSerializer implements ISerializer {

	protected JSONUtils ju;

	public GSONSerializer(){
		ju = new JSONUtils();
	}

	@Override
	public String serialize(Object object) {
		return ju.gson.toJson(object);
	}

	@Override
	public String getMediaType() {
		return "application/json";
	}

	@Override
	public <T> T parse(String input, Type type) {
		JsonReader jr = new JsonReader(new StringReader(input));
		jr.setLenient(true);
		return ju.gson.fromJson(jr, type);
	}

	@Override
	public JsonElement getRaw(Object object){
		return ju.gson.toJsonTree(object);
	}
}
