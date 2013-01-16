package com.datascience.gal.decision;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.datascience.gal.Category;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.Worker;
import com.datascience.gal.decision.ILabelProbabilityDistributionCostCalculator;
import com.datascience.gal.decision.WorkerQualityCalculator;
import com.datascience.utils.Utils;

public class WorkerEstimator extends WorkerQualityCalculator{

	public WorkerEstimator(ILabelProbabilityDistributionCostCalculator lpdcc) {
		super(lpdcc);
	}

	@Override
	public double getError(DawidSkene ds, Worker w, String from, String to) {
		return ds.getErrorRateForWorker(w, from, to);
	}

	public Map<String, Object> getScore(DawidSkene ds, Worker w){
		WorkerEstimator we_exp = new WorkerEstimator(new LabelProbabilityDistributionCostCalculators.ExpectedCostAlgorithm());
		WorkerEstimator we_min = new WorkerEstimator(new LabelProbabilityDistributionCostCalculators.SelectedLabeBased(
				new ObjectLabelDecisionAlgorithms.MinCostDecisionAlgorithm()));
		int contributions = w.getAssignedLabels().size();
		int gold_tests = w.countGoldTests(ds.getObjects());

		Map<String, Object> m = new HashMap<String, Object>();
		m.put("Worker", w.getName());
		m.put("Error rate", getCostStr(getAnnotatorCostNaive(ds, w), false));
		m.put("Quality (Expected)", getCostStr(we_exp.getCost(ds, w), true));
		m.put("Quality (Optimized)", getCostStr(we_min.getCost(ds, w), true));
		m.put("Number of Annotations", contributions);
		m.put("Number of Gold Tests", gold_tests);
		LinkedList<Map<String, Object>> matrix = new LinkedList<Map<String, Object>>();
		for (String correct_name : ds.getCategories().keySet()) {
			for (String assigned_name : ds.getCategories().keySet()) {
				Map<String, Object> val = new HashMap<String, Object>();
				val.put("from", correct_name);
				val.put("to", assigned_name);
				double cm_entry = ds.getErrorRateForWorker(w, correct_name, assigned_name);
				String s_cm_entry = Double.isNaN(cm_entry) ? "---" : Utils.round(100 * cm_entry, 3).toString();
				val.put("value", s_cm_entry);
				matrix.add(val);
			}
		}
		m.put("Confusion Matrix", matrix);
		return m;
	}
	
	private double getAnnotatorCostNaive(DawidSkene ds, Worker w) {
		double c = 0.0;
		double s = 0.0;
		for (Category from : ds.getCategories().values()) {
			for (Category to : ds.getCategories().values()) {
				double fromPrior = ds.prior(from.getName());
				c += fromPrior
					 * from.getCost(to.getName())
					 * ds.getErrorRateForWorker(w, from.getName(), to.getName());
				s += fromPrior * from.getCost(to.getName());
			}
		}
		return (s > 0) ? c / s : 0.0;
	}
}
