package com.ipeirotis.gal;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.ipeirotis.gal.service.JSONUtils;

public class CategoryPair {
    public static final CategoryPairDeserializer deserializer = new CategoryPairDeserializer();
    public static final CategoryPairSerializer serializer = new CategoryPairSerializer();

    private String from;
    private String to;

    public CategoryPair(String from, String to) {
        this.from = from;
        this.to = to;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((from == null) ? 0 : from.hashCode());
        result = prime * result + ((to == null) ? 0 : to.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof CategoryPair))
            return false;
        CategoryPair other = (CategoryPair) obj;
        if (from == null) {
            if (other.from != null)
                return false;
        } else if (!from.equals(other.from))
            return false;
        if (to == null) {
            if (other.to != null)
                return false;
        } else if (!to.equals(other.to))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return JSONUtils.gson.toJson(this);
    }

    /**
     * this is being a pain in the ass hacked together for now, probably need to
     * revise the serialization in ConfusionMatrix to really fix
     * 
     * @author josh
     * 
     */
    public static class CategoryPairDeserializer implements
            JsonDeserializer<CategoryPair> {

        @Override
        public CategoryPair deserialize(JsonElement json, Type type,
                JsonDeserializationContext context) throws JsonParseException {
            JsonObject jobject;
            if (json.isJsonPrimitive()) {
                String str = json.getAsString();
                return JSONUtils.gson.fromJson(str, JSONUtils.categoryPairType);
            } else {
                jobject = (JsonObject) json;
                return new CategoryPair(jobject.get("from").getAsString(),
                        jobject.get("to").getAsString());

            }
        }

    }

    public static class CategoryPairSerializer implements
            JsonSerializer<CategoryPair> {

        @Override
        public JsonElement serialize(CategoryPair src, Type type,
                JsonSerializationContext context) {
            JsonObject jobject = new JsonObject();
            jobject.addProperty("to", src.to);
            jobject.addProperty("from", src.from);
            return jobject;
        }

    }
}
