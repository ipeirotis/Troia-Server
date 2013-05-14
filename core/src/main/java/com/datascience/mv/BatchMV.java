package com.datascience.mv;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.CategoryPriorCalculators;
import com.datascience.core.stats.ErrorRateCalculators;

import java.util.HashMap;

/**
 * @Author: konrad
 */
public class BatchMV extends MajorityVote {

	public BatchMV(){
		super(
			new ErrorRateCalculators.BatchErrorRateCalculator(),
			new CategoryPriorCalculators.BatchCategoryPriorCalculator());
	}

	@Override
	public void compute() {
		computeForObjects();
		computeForWorkers();
		computeCategoryPriorsIfNeeded();
	}

	public void computeForObjects(){
		for (LObject<String> object: getData().getObjects()){
			computeResultsForObject(object);
		}
	}

	public void computeForWorkers(){
		for (Worker<String> worker: getData().getWorkers()){
			computeWorkersConfusionMatrix(worker);
		}
	}

	public void computeCategoryPriorsIfNeeded(){
		if (!data.arePriorsFixed()){
			HashMap<String, Double> categoryPriors = new HashMap<String, Double>();
			for (String c : data.getCategories())
				categoryPriors.put(c, 0.);
			for (AssignedLabel<String> al : data.getAssigns())
				categoryPriors.put(al.getLabel(), categoryPriors.get(al.getLabel()) + 1);
			for (String c : data.getCategories())
				model.categoryPriors.put(c, categoryPriors.get(c) / data.getAssigns().size());
		}
	}
}
