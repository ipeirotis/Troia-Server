package com.datascience.gal.generator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.datascience.gal.AssignedLabel;
import com.datascience.gal.BatchDawidSkene;
import com.datascience.gal.Category;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.MisclassificationCost;
import com.datascience.gal.Worker;

/**
 * @author Michael Arshynov
 *
 */
public class TestDatum {
	public static final double DELTA_DOUBLE = 0.01;
	
	private Set<Category> categorySet = null;
	private List<Worker> workerList = null;
	private Set<CorrectLabel> correctLabelSet = null;
	private Set<AssignedLabel> assignedLabelSet = null;
	private Set<MisclassificationCost> costSet = null;
	private DawidSkene ds = null;
	private int iterationsInt = 1;
	
	/**
	 * @param categoryName
	 * @param probability
	 */
	public void addCategory(String categoryName, double probability) {
		if (categorySet==null) categorySet = new HashSet<Category>();
		Category category = new Category(categoryName);
//		category.setCost(to, cost)
		category.setPrior(probability);
		categorySet.add(category);
	}
	
	/**
	 * @return
	 */
	public boolean validateCategorySet() {
		double bit = 1.0;
		for (Category category: categorySet) {
			bit-=category.getPrior();
		}
		return (DELTA_DOUBLE>bit && bit>=0);
	}
	
	/**
	 * 
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
	 * @param string
	 */
	private void addWorker(String name) {
		if (workerList == null) workerList = new ArrayList<Worker>();
		Worker worker = new Worker(name, categorySet);
		workerList.add(worker);
	}
	
	
	/**
	 * @param labelName
	 * @param categoryName
	 */
	public void addCorrectLabel(String labelName, String categoryName) {
		 if (correctLabelSet==null) correctLabelSet = new HashSet<CorrectLabel>();
		 CorrectLabel correctLabel = new CorrectLabel(labelName, categoryName);
		 correctLabelSet.add(correctLabel);
	}
	
	/**
	 * @param workerName
	 * @param objectName
	 * @param categoryName
	 */
	public void addAssignedLabel(String workerName, String objectName, String categoryName) {
		if (assignedLabelSet == null) assignedLabelSet = new HashSet<AssignedLabel>();
		AssignedLabel assignedLabel = new AssignedLabel(workerName, objectName, categoryName);
		assignedLabelSet.add(assignedLabel);
	}
	
	/**
	 * @param from
	 * @param to
	 * @param cost
	 */
	public void addCost(String from, String to, double value) {
		if (costSet == null) costSet = new HashSet<MisclassificationCost>();
		MisclassificationCost cost = new MisclassificationCost(from, to, value);
		costSet.add(cost);
	}
	
	public void compute() {
		if (ds == null)
		ds = new BatchDawidSkene("", categorySet);
		ds.addAssignedLabels(assignedLabelSet);
		ds.addCorrectLabels(correctLabelSet);
		ds.addMisclassificationCosts(costSet);

		boolean detailed = false;
		
		String majority = ds.printVote();
		System.out.println(majority);
        for (int i = 0; i < iterationsInt; i++) {
            // ds.estimate(iterations);
            ds.estimate(1);
        }
		
		String summary_report = ds.printAllWorkerScores(detailed);
		System.err.println(summary_report);
        
//        for (Worker w:ds.getWorkerCost(w, method)) 
//        System.out.println(ds.printWorkerScore(w, detailed));
	}
	/**
	 * 
	 */
	public void construct() {

		iterationsInt = 10;
		
		addCategory("notporn", 0.1);
		addCategory("softporn", 0.4);
		addCategory("hardporn", 0.5);
		
		validateCategorySet();
//		setUsualCosts();
		
		addWorker("worker1");
		addWorker("worker2");
		addWorker("worker3");
		addWorker("worker4");
		
		addCorrectLabel("url1", "notporn");
		addCorrectLabel("url2", "softporn");
		addCorrectLabel("url3", "hardporn");
		
		addAssignedLabel("worker1", "url1", "notporn");	//Error Rate 0.0%
		addAssignedLabel("worker1", "url2", "softporn");
		addAssignedLabel("worker1", "url3", "hardporn");
		
		addAssignedLabel("worker2", "url1", "notporn"); //Error Rate 25.0%
		addAssignedLabel("worker2", "url2", "softporn");
		addAssignedLabel("worker2", "url3", "notporn");
		
		addAssignedLabel("worker3", "url1", "notporn");	//Error Rate 45.0%
		addAssignedLabel("worker3", "url2", "hardporn");
		addAssignedLabel("worker3", "url3", "notporn");
		
		addAssignedLabel("worker4", "url1", "softporn");//Error Rate 50.0%
		addAssignedLabel("worker4", "url2", "hardporn");
		addAssignedLabel("worker4", "url3", "notporn");
		
		addCost("notporn", "notporn", 0.0);
		addCost("softporn", "notporn", 1);
		addCost("hardporn", "notporn", 1);
		
		addCost("notporn", "softporn", 1);
		addCost("softporn", "softporn", 0.0);
		addCost("hardporn", "softporn", 1);
		
		addCost("notporn", "hardporn", 1);
		addCost("softporn", "hardporn", 1);
		addCost("hardporn", "hardporn", 0.0);
		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestDatum testCase = new TestDatum();
		testCase.construct();
		testCase.compute();
		
//		ON SCREEN 
//		Worker	Error Rate	Quality (Expected)	Quality (Optimized)	Number of Annotations	Gold Tests
//		worker1	0.0%	100%	100%	3	3
//		worker2	25.0%	---	---	3	3
//		worker3	45.0%	---	---	3	3
//		worker4	50.0%	100%	100%	3	3
	}

}
