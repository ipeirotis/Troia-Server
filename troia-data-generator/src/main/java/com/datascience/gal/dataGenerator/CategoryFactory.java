package com.datascience.gal.dataGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.datascience.gal.AssignedLabel;
import com.datascience.gal.Category;

/**
 * This factory is used for creation of Category objects
 * whith their names as input. It's also possible to
 * create categories with specified misclassification cost
 *
 * @author piotr.gnys@10clouds.com
 */
public class CategoryFactory {


	/**
	 *  Creates labels with given names and default misclassification costs
	 * @param labelNames Names of labels
	 * @return labels with given names and default misclassification costs
	 */
	public Collection<Category> createCategories(Collection<String> labelNames) {
		Collection<Category> labels=new ArrayList<Category>();
		for (String labelName : labelNames) {
			Category l = new Category(labelName);
			for (String otherAssignedLabel : labelNames) {
				if(!otherAssignedLabel.equalsIgnoreCase(labelName)) {
					l.setCost(otherAssignedLabel, 1);
				} else {
					l.setCost(otherAssignedLabel, 0);
				}
			}
			labels.add(l);
		}
		return labels;
	}

	public Collection<Category> createCategories(Collection<String> labelNames,Map<String,Double>priorities,
			Map<String,Map<String,Double>> misclassificationMatrix) {
		Collection<Category> labels=new ArrayList<Category>();
		for (String labelName : labelNames) {
			Category l = new Category(labelName);
			l.setPrior(priorities.get(labelName));
			for (String s : misclassificationMatrix.get(labelName).keySet()){
				l.setCost(s, misclassificationMatrix.get(labelName).get(s));
			}
			labels.add(l);
		}
		return labels;
	}


	public Collection<Category> createCategories(Collection<String> labelNames,Map<String,Map<String,Double>> misclassificationMatrix) {
		Collection<Category> labels=new ArrayList<Category>();
		for (String labelName : labelNames) {
			Category l = new Category(labelName);
			for (String s : misclassificationMatrix.get(labelName).keySet()){
				l.setCost(s, misclassificationMatrix.get(labelName).get(s));
			}
			labels.add(l);
		}
		return labels;
	}



	public Collection<Category> extractCategories(Collection<AssignedLabel> labels) {
		Collection<String> categoryNames = new ArrayList<String>();
		for (AssignedLabel label : labels) {
			if(!categoryNames.contains(label.getCategoryName())) {
				categoryNames.add(label.getCategoryName());
			}
		}
		return this.createCategories(categoryNames);
	}


	public Collection<Category> extractCategories(Collection<AssignedLabel> labels,Map<String,Map<String,Double>> misclassificationMatrix) {
		Collection<String> categoryNames = new ArrayList<String>();
		for (AssignedLabel label : labels) {
			if(!categoryNames.contains(label.getCategoryName())) {
				categoryNames.add(label.getCategoryName());
			}
		}
		return this.createCategories(categoryNames,misclassificationMatrix);
	}


	/**
	 * @return Instance of label factory
	 */
	public static CategoryFactory getInstance() {
		return instance;
	}



	private static CategoryFactory instance = new CategoryFactory();

	private CategoryFactory() {

	}
}