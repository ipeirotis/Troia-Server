package com.datascience.galc;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Set;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import org.apache.log4j.Logger;

import com.datascience.core.storages.JSONUtils;
import com.google.common.base.Objects;

public class WorkerCont extends Worker{
	
	public static final WorkerDeserializer deserializer = new WorkerDeserializer();
	private WorkerContResults 				results;

	private static Logger logger = Logger.getLogger(WorkerCont.class);
	
	public WorkerCont(String name) {

		super(name);
		results  = new WorkerContResults();
		
	}

	public WorkerContResults getResults() {
		return results;
	}

	public void setResults(WorkerContResults results) {
		this.results = results;
	}

	public void computeZetaValues() {
		Set<AssignedLabel> labels = getLabels();
		int n = labels.size();
		double mu_worker = 0.0;
		double mu_square = 0.0;
		for (AssignedLabel al : labels) {
			mu_worker += al.getLabel();
			mu_square += Math.pow(al.getLabel(), 2);
		}

		results.setEst_mu(mu_worker/n);
		results.setEst_sigma(Math.sqrt((1.0 / n) * (mu_square - Math.pow(mu_worker, 2) / n)));
		//logger.info(this.toString());
		if(results.getEst_sigma()==0.0) {
			results.setEst_sigma(0.00000000001);
			logger.warn("[Single Label Worker: " +this.getName()+"]");
		}

		for (AssignedLabel al : labels) {
			Double z = (al.getLabel() - results.getEst_mu()) / results.getEst_sigma();
			AssignedLabel zl = new AssignedLabel(al.getWorkerName(), al.getObjectName(), z);
			results.getZetaValues().add(zl);
		}
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			       .add("name", getName())
			       .add("est_rho", results.getEst_rho())
			       .add("est_mu", results.getEst_mu())
			       .add("est_sigma", results.getEst_sigma())
			       .toString();
	}
	

public static class WorkerDeserializer implements JsonDeserializer<Worker> {

	@Override
	public Worker deserialize(JsonElement json, Type type,
							  JsonDeserializationContext context) throws JsonParseException {
		JsonObject jobject = (JsonObject) json;
		String name = jobject.get("name").getAsString();
		Double est_rho = jobject.get("est_rho").getAsDouble();
		Double est_mu = jobject.get("est_mu").getAsDouble();
		Double est_sigma = jobject.get("est_sigma").getAsDouble();
		Collection<AssignedLabel> labels = JSONUtils.gson.fromJson(
											   jobject.get("labels"), JSONUtils.assignedLabelSetType);

		return new WorkerCont(name);
		}
	}
}
