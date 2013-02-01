package com.datascience.galc.serialization;

import com.datascience.core.base.AssignedLabel;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;

/**
 *
 * @author Michał Borysiak
 */
public class GenericAssignedLabelDeserializer<T> implements JsonDeserializer<AssignedLabel<T>> {
	
	@Override
	public AssignedLabel<T> deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jdc) throws JsonParseException {
		return null;
	}
		
}
