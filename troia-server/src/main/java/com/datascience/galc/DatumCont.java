package com.datascience.galc;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import com.datascience.core.storages.JSONUtils;
import com.datascience.utils.objects.Datum;
import com.google.common.base.Objects;

public class DatumCont extends Datum {
	public static final DatumDeserializer 	deserializer = new DatumDeserializer();	
	private DatumContResults 				results;

	public DatumCont(String name) {
		super(name);
		this.results = new DatumContResults();	
	}

	public DatumContResults getResults() {
		return results;
	}

	public void setResults(DatumContResults results) {
		this.results = results;
	}

	/**
	 * @return the est_value
	 */
	public Double getAverageLabel() {

		double sum =0;
		Set<AssignedLabel> labels = getAssignedLabels();
		for (AssignedLabel al: labels) {
			sum += al.getLabel();
		}
		
		return sum/labels.size();
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
//	@Override
//	public String toString() {
//		DatumContResults dr = getResults();
//		return Objects.toStringHelper(this)
//			       .add("name", getName())
//			       .add("est_value", dr.getEst_value())
//			       .add("est_zeta", dr.getEst_zeta())
//			       .toString();
//	}

	@Override
	public String toString() {
		return JSONUtils.gson.toJson(this);
	}

	public static class DatumDeserializer implements JsonDeserializer<Datum> {

		@Override
		public Datum deserialize(JsonElement json, Type type,
								 JsonDeserializationContext context) throws JsonParseException {
			JsonObject jobject = (JsonObject) json;
			String name = jobject.get("name").getAsString();
			Double est_value = jobject.get("est_value").getAsDouble();
			Double est_zeta = jobject.get("est_zeta").getAsDouble();
			Set<AssignedLabel> labels = JSONUtils.gson.fromJson(
												   jobject.get("labels"), JSONUtils.assignedLabelSetType);

			if (jobject.has("correctCategory")) {
				return new DatumCont(name);
			} else {
				return new DatumCont(name);
			}
		}
	}

}

