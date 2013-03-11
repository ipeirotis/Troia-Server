package com.datascience.mv;

import com.datascience.core.base.Algorithm;
import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.NominalData;
import com.datascience.gal.DatumResult;
import com.datascience.gal.WorkerResult;
import com.datascience.utils.ProbabilityDistributions;

import java.util.Collection;
import java.util.Map;

/**
 * @Author: konrad
 */
public abstract class MajorityVote extends Algorithm<String, NominalData, DatumResult, WorkerResult> {

	public void computeResultsForObject(LObject<String> object){
		DatumResult dr = results.getOrCreateDatumResult(object);
		dr.setCategoryProbabilites(objectLabelDistribution(object));
	}

	public Map<String, Double> objectLabelDistribution(LObject<String> object){
		Collection<String> categories = data.getCategoriesNames();
		if (object.isGold()) {
			return ProbabilityDistributions.generateGoldDistribution(categories, object.getGoldLabel());
		}
		Collection<AssignedLabel<String>> assigns = data.getAssignsForObject(object);
		if (assigns.isEmpty()) {
			return generateLabelForNonAssignedObject();
		}
		return generateLabelDistribution(categories, assigns);
	}

	public Map<String, Double> generateLabelForNonAssignedObject(){
		return ProbabilityDistributions.getPriorBasedDistribution(data);
	}

	public Map<String, Double> generateLabelDistribution(Collection<String> categories,
				Collection<AssignedLabel<String>> assigns){
		Map<String, Double> pd = ProbabilityDistributions.generateConstantDistribution(categories, 0.);
		Double base = 1. / assigns.size();
		for (AssignedLabel<String> assign: assigns) {
			String label = assign.getLabel();
			pd.put(label, pd.get(label) + base);
		}
		return pd;
	}
}
