package com.datascience.gal.generator;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.datascience.gal.AssignedLabel;
import com.datascience.gal.BatchDawidSkene;
import com.datascience.gal.Category;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.MisclassificationCost;
import com.datascience.gal.Worker;
import com.datascience.gal.service.ComputerHelper;

/**
 * @author Michael Arshynov
 *
 */
public class TestDatum extends TestDatumSuper {

	public TestDatum() {
		super();
	}

	private DawidSkene ds = null;
	private int iterationsInt = 10;
	

	public StringBuffer compute() {
		final String HEADER = new String("\n\n\n==============================Output=========================<<<<<<<<<<<<");
		final String FOOTER = new String("\n>>>>>>>>>>>>===================Output====================================");
		if (ds == null)
		ds = new BatchDawidSkene("", categorySet);
		boolean verbose = true;
		ds.addAssignedLabels(assignedLabelSet);
		ds.addCorrectLabels(correctLabelSet);
		ds.addMisclassificationCosts(costSet);

        for (int i = 0; i < iterationsInt; i++) {
            ds.estimate(1);
        }
        
		Map<String, String> prior_voting = ds.getMajorityVote();
		outMajorityVote = ComputerHelper.saveMajorityVote(verbose, ds);
		outWorkerQuality = ComputerHelper.saveWorkerQuality(verbose, ds);
		outObjectResults = ComputerHelper.saveObjectResults(verbose, ds);
		outCategoryPriors = ComputerHelper.saveCategoryPriors(verbose, ds);
		outDawidSkeneVote = ComputerHelper.saveDawidSkeneVote(verbose, ds);
	  
		Map<String, String> posterior_voting = ds.getMajorityVote();
		outDifferences = ComputerHelper.saveDifferences(verbose, ds, prior_voting, posterior_voting);
		
		return new StringBuffer(HEADER)
				.append(outMajorityVote)
				.append(outWorkerQuality)
				.append(outObjectResults)
				.append(outCategoryPriors)
				.append(outDawidSkeneVote)
				.append(FOOTER);
	}

	/**
	 * @param input
	 */
	public void construct(InputConditionsForTest input) {
		iterationsInt = input.getIterationsInt();
		
		//Categories Setup
		for (String categoryName: input.getCategoryMap().keySet()) {
			addCategory(categoryName, input.getCategoryMap().get(categoryName));
		}
		
		validateCategorySet();
		setUsualCosts();
		
		//Workers Setup 
		for (String workerName:input.getWorkerMap().keySet()) {
			addWorker(workerName, input.getWorkerMap().get(workerName));
		}
		
		//Correct Answers Setup
		for (String goldName:input.getGoldMap().keySet()) {
			addCorrectLabel(goldName, input.getGoldMap().get(goldName));
		}
		
		//Answers Setup
		for (String workerName: input.getAnswerMap().keySet()) {
			Map<String, String> answers = input.getAnswerMap().get(workerName);
			for (String objectName: answers.keySet()) {
				addAssignedLabel(workerName, objectName, answers.get(objectName));
			}
		}
		//Costs Setup
		Set<String> categories = input.getCategoryMap().keySet();
		Set<String> categoriesClone = new HashSet<String>(categories);
		for (String row:categories) {
			for (String column:categoriesClone) {
				double cost = 1.0;
				if (row.equals(column)) cost = 0.0;
				addCost(row, column, cost);
			}
		}
		
/*
		iterationsInt = 10;
		
		addCategory("notporn", 0.1);
		addCategory("softporn", 0.4);
		addCategory("hardporn", 0.5);
		
		validateCategorySet();
		setUsualCosts();
		
		Double[][] confusionMatrix = new Double[][]{{0.7,0.2,0.1},{0.4, 0.5,0.1},{0.3,0.3,0.4}};
		
		addWorker("worker1", confusionMatrix);
		addWorker("worker2", confusionMatrix);
		addWorker("worker3", confusionMatrix);
		addWorker("worker4", confusionMatrix);
		
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
		
		*/
//		
//		for (Category o:categorySet) {
//			System.err.println(o);
//		}
//		for (Worker o:workerList) {
//			System.err.println(o);
//		}
//		for (CorrectLabel o:correctLabelSet) {
//			System.err.println(o);
//		}
//		for (AssignedLabel o:assignedLabelSet) {
//			System.err.println(o);
//		}


		
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestDatum testCase = new TestDatum();
		testCase.construct(null);
		System.out.println(testCase.compute());
	}

}
