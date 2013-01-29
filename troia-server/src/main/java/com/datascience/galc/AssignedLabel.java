package com.datascience.galc;

import java.lang.reflect.Type;

import com.datascience.core.storages.JSONUtils;
import com.datascience.gal.AssignedLabel.AssignedLabelDeserializer;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

public class AssignedLabel {

	public static final AssignedLabelDeserializer deserializer = new AssignedLabelDeserializer();
	private String	worker_name;
	private String	object_name;
	private Double	label;

	public AssignedLabel(String w, String o, Double label) {

		this.worker_name = w;
		this.object_name = o;
		this.label = label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode( this.label, this.object_name, this.worker_name); 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AssignedLabel))
			return false;
		AssignedLabel other = (AssignedLabel) obj;
		return Objects.equal(this.label, other.label) 
			&& Objects.equal(this.object_name, other.object_name)
			&& Objects.equal(this.worker_name, other.worker_name);
	}

	/**
	 * @return the workerName
	 */
	public String getWorkerName() {

		return worker_name;
	}

	/**
	 * @return the objectName
	 */
	public String getObjectName() {

		return object_name;
	}

	/**
	 * @return the label
	 */
	public Double getLabel() {

		return label;
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
										 "label").getAsDouble());
		}

	}	
	
}
