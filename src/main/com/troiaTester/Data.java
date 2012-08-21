package main.com.troiaTester;

import java.util.Collection;

import troiaClient.Category;
import troiaClient.GoldLabel;
import troiaClient.Label;
import troiaClient.MisclassificationCost;

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
	public Collection<Category> getCategories() {
		return categories;
	}

	/**
	 * @param categories Collection of category names
	 */
	public void setCategories(Collection<Category> categories) {
		this.categories = categories;
	}

	/**
	 * @return Collection of generated labels
	 */
	public Collection<Label> getLabels() {
		return labels;
	}

	/**
	 * @param labels Collection of labels
	 */
	public void setLabels(Collection<Label> labels) {
		this.labels = labels;
	}

	/**
	 * @return Collection of gold labels in request
	 */
	public Collection<GoldLabel> getGoldLabels() {
		return goldLabels;
	}

	/**
	 * @param goldLabels Collection of gold labels in request
	 */
	public void setGoldLabels(Collection<GoldLabel> goldLabels) {
		this.goldLabels = goldLabels;
	}

	
	
	/**
	 * @return Collection of misclassification costs
	 */
	public Collection<MisclassificationCost> getMisclassificationCost() {
		return misclassificationCost;
	}

	/**
	 * @param misclassificationCost Collection of misclassification costs
	 */
	public void setMisclassificationCost(
			Collection<MisclassificationCost> misclassificationCost) {
		this.misclassificationCost = misclassificationCost;
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
	Collection<Category> categories;
	
	/**
	 * Collection of generated labels
	 */
	Collection<Label> labels; 
	
	/**
	 * Collection of gold labels in request
	 */
	Collection<GoldLabel> goldLabels;
	
	/**
	 * Collection of misclassification costs
	 */
	Collection<MisclassificationCost> misclassificationCost;
	
	Collection<ArtificialWorker> artificialWorkers;
}
