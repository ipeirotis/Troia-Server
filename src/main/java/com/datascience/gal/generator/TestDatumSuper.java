package com.datascience.gal.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.datascience.gal.AssignedLabel;
import com.datascience.gal.Category;
import com.datascience.gal.ConfusionMatrix;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.MisclassificationCost;
import com.datascience.gal.Worker;

/**
 * @author Michael Arshynov
 *
 */
public abstract class TestDatumSuper {
	private static final double DELTA_DOUBLE = 0.01;
	protected Set<Category> categorySet = null;
	protected List<Worker> workerList = null;
	protected Set<CorrectLabel> correctLabelSet = null;
	protected Set<AssignedLabel> assignedLabelSet = null;
	protected Set<MisclassificationCost> costSet = null;

	protected StringBuffer outMajorityVote = null;
	protected StringBuffer outWorkerQuality = null;
	protected StringBuffer outObjectResults = null;
	protected StringBuffer outCategoryPriors = null;
	protected StringBuffer outDawidSkeneVote = null;
	protected StringBuffer outDifferences = null;

	protected StringBuffer outResults = null;
	protected StringBuffer outInputs = null;
	/**
	 * Setup categorySet, for right answer 0.0, for wrong 1.0
	 */
	public void setUsualCosts() {
		for (Category category: categorySet) {
			String categoryToCompareWithName = category.getName();
			for (Category otherCategory: categorySet) {
				double cost = 1.0;
				if (categoryToCompareWithName.equals(otherCategory.getName())) cost = 0.0;
				otherCategory.setCost(categoryToCompareWithName, cost);
			}
		}
	}
	/**
	 * @param cm
	 * @param doubles
	 */
	private void fillUpConfusionMatrix(ConfusionMatrix cm, Double[][] doubles) {
		int f=0;
		int t=0;
		for (Category from:categorySet) {
			t=0;
			for (Category to:categorySet) {
				cm.setErrorRate(from.getName(), to.getName(), doubles[t][f]);
				t++;
			}
			f++;
		}
	}

	/**
	 * @param string
	 */
	protected void addWorker(String name, Double[][] doubles) {
		if (workerList == null) workerList = new ArrayList<Worker>();
		Worker worker = new Worker(name, categorySet);
		fillUpConfusionMatrix(worker.cm, doubles);
		workerList.add(worker);
	}

	/**
	 * @param labelName
	 * @param categoryName
	 */
	protected void addCorrectLabel(String labelName, String categoryName) {
		if (correctLabelSet==null) correctLabelSet = new HashSet<CorrectLabel>();
		CorrectLabel correctLabel = new CorrectLabel(labelName, categoryName);
		correctLabelSet.add(correctLabel);
	}

	/**
	 * @param categoryName
	 * @param probability
	 */
	protected void addCategory(String categoryName, double probability) {
		if (categorySet==null) categorySet = new LinkedHashSet<Category>();
		Category category = new Category(categoryName);
		category.setPrior(probability);
		categorySet.add(category);
	}

	/**
	 * @return
	 */
	protected boolean validateCategorySet() {
		double bit = 1.0;
		for (Category category: categorySet) {
			bit-=category.getPrior();
		}
		return (DELTA_DOUBLE>bit && bit>=0);
	}

	/**
	 * @param workerName
	 * @param objectName
	 * @param categoryName
	 */
	protected void addAssignedLabel(String workerName, String objectName, String categoryName) {
		if (assignedLabelSet == null) assignedLabelSet = new HashSet<AssignedLabel>();
		AssignedLabel assignedLabel = new AssignedLabel(workerName, objectName, categoryName);
		assignedLabelSet.add(assignedLabel);
	}

	/**
	 * @param from
	 * @param to
	 * @param cost
	 */
	protected void addCost(String from, String to, double value) {
		if (costSet == null) costSet = new HashSet<MisclassificationCost>();
		MisclassificationCost cost = new MisclassificationCost(from, to, value);
		costSet.add(cost);
	}

}
