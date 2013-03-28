package com.datascience.gal.dataGenerator;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.utils.CostMatrix;

import java.util.Collection;
import java.util.Map;

public class Data {

	/**
	 * @return Request identifier
	 */
	public String getRequestId() {
		return requestId;
	}


	/**
	 * @param requestId Request identifier
	 */
	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	/**
	 * @return Collection of worker names
	 */
	public Collection<String> getWorkers() {
		return workers;
	}

	/**
	 * @param workers Collection of worker names
	 */
	public void setWorkers(Collection<String> workers) {
		this.workers = workers;
	}

	/**
	 * @return Collection of object names
	 */
	public Collection<String> getObjects() {
		return objectCollection;
	}

	/**
	 * @return the objectCollection
	 */
	public TroiaObjectCollection getObjectCollection() {
		return objectCollection;
	}

	/**
	 * @param objectCollection the objectCollection to set
	 */
	public void setObjectCollection(TroiaObjectCollection objectCollection) {
		this.objectCollection = objectCollection;
	}

	/**
	 * @return Collection of category names
	 */
	public Collection<String> getCategories() {
		return categories;
	}

	/**
	 * @param categories Collection of category names
	 */
	public void setCategories(Collection<String> categories) {
		this.categories = categories;
	}

	/**
	 * @return Collection of generated labels
	 */
	public Collection<AssignedLabel<String>> getLabels() {
		return labels;
	}

	/**
	 * @param labels Collection of labels
	 */
	public void setLabels(Collection<AssignedLabel<String>> labels) {
		this.labels = labels;
	}

	/**
	 * @return Collection of gold labels in request
	 */
	public Collection<LObject<String>> getGoldLabels() {
		return goldLabels;
	}

	/**
	 * @param goldLabels Collection of gold labels in request
	 */
	public void setGoldLabels(Collection<LObject<String>> goldLabels) {
		this.goldLabels = goldLabels;
	}


	public CostMatrix<String> getCostMatrix() {
		return costMatrix;
	}

	public void setCostMatrix(CostMatrix<String> misclassificationCost) {
		this.costMatrix = misclassificationCost;
	}

	/**
	 * @return the artificialWorkers
	 */
	public Collection<ArtificialWorker> getArtificialWorkers() {
		return artificialWorkers;
	}

	/**
	 * @param artificialWorkers the artificialWorkers to set
	 */
	public void setArtificialWorkers(Collection<ArtificialWorker> artificialWorkers) {
		this.artificialWorkers = artificialWorkers;
	}


	/**
	 * @return the artificialWorkerQualities
	 */
	public Collection<Map<String, Object>> getArtificialWorkerQualities() {
		return artificialWorkerQualities;
	}

	/**
	 * @param artificialWorkerQualities the artificialWorkerQualities to set
	 */
	public void setArtificialWorkerQualities(Collection<Map<String, Object>> artificialWorkerQualities) {
		this.artificialWorkerQualities = artificialWorkerQualities;
	}



	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return "Data { " +
			   "requestId = " + requestId + ", " +
			   "workers = " + workers + ", " +
			   "objectCollection = " + objectCollection + ", " +
			   "categories = " + categories + ", " +
			   "labels = " + labels + ", " +
			   "goldLabels = " + goldLabels + ", " +
			   "costMatrix = " + costMatrix + ", " +
			   "artificialWorkers = " + artificialWorkers + ", " + "}";
	}

	/**
	 * Request identifier
	 */
	String requestId;

	/**
	 * Collection of worker names
	 */
	Collection<String> workers;

	/**
	 * Collection like object holding object names with associated categories
	 */
	TroiaObjectCollection objectCollection;

	/**
	 * Collection of category names
	 */
	Collection<String> categories;

	/**
	 * Collection of generated labels
	 */
	Collection<AssignedLabel<String>> labels;

	/**
	 * Collection of gold labels in request
	 */
	Collection<LObject<String>> goldLabels;

	/**
	 * Collection of misclassification costs
	 */
	CostMatrix<String> costMatrix;

	Collection<ArtificialWorker> artificialWorkers;

	Collection<Map<String, Object>> artificialWorkerQualities;
}
