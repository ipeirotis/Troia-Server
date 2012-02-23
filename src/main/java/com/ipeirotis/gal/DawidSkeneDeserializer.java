package com.ipeirotis.gal;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.ipeirotis.gal.BatchDawidSkene.BatchDawidSkeneDeserializer;
import com.ipeirotis.gal.IncrementalDawidSkene.IncrementalDawidSkeneDeserializer;

/**
 * deserialization for arbitrary ds classes TODO: doesnt work yet
 * 
 * @author josh
 * 
 */
public class DawidSkeneDeserializer implements JsonDeserializer<DawidSkene> {
    public static final DawidSkeneDeserializer deserializer = new DawidSkeneDeserializer();
    private static final IncrementalDawidSkeneDeserializer incrementalDeserializer = new IncrementalDawidSkeneDeserializer();
    private static final BatchDawidSkeneDeserializer batchDeserializer = new BatchDawidSkeneDeserializer();

    @Override
    public DawidSkene deserialize(JsonElement json, Type type,
            JsonDeserializationContext context) throws JsonParseException {
        JsonObject jobject = (JsonObject) json;
        if (jobject.has("dsmethod"))
            return incrementalDeserializer.deserialize(json, type, context);
        else
            return batchDeserializer.deserialize(json, type, context);
    }

}
