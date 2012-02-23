package com.ipeirotis.gal;

import java.lang.reflect.Type;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.ipeirotis.gal.service.JSONUtils;

public class AssignedLabel {

    public static final AssignedLabelDeserializer deserializer = new AssignedLabelDeserializer();
    private String workerName;
    private String objectName;
    private String categoryName;

    /**
     * @return the workerName
     */
    public String getWorkerName() {
        return workerName;
    }

    /**
     * @return the objectName
     */
    public String getObjectName() {
        return objectName;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
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
        result = prime * result
                + ((objectName == null) ? 0 : objectName.hashCode());
        result = prime * result
                + ((workerName == null) ? 0 : workerName.hashCode());
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
        if (!(obj instanceof AssignedLabel))
            return false;
        AssignedLabel other = (AssignedLabel) obj;
        if (objectName == null) {
            if (other.objectName != null)
                return false;
        } else if (!objectName.equals(other.objectName))
            return false;
        if (workerName == null) {
            if (other.workerName != null)
                return false;
        } else if (!workerName.equals(other.workerName))
            return false;
        return true;
    }

    public AssignedLabel(String w, String d, String c) {
        this.workerName = w;
        this.objectName = d;
        this.categoryName = c;
    }

    @Override
    public String toString() {
        return JSONUtils.gson.toJson(this);
    }

    public static class AssignedLabelDeserializer implements
            JsonDeserializer<AssignedLabel> {

        @Override
        public AssignedLabel deserialize(JsonElement json, Type type,
                JsonDeserializationContext context) throws JsonParseException {
            JsonObject jobject = (JsonObject) json;
            return new AssignedLabel(jobject.get("workerName").getAsString(),
                    jobject.get("objectName").getAsString(), jobject.get(
                            "categoryName").getAsString());
        }

    }
}
