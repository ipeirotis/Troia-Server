package com.datascience.core.nominal;

import java.util.HashMap;
import java.util.Map;

/**
 * User: artur
 * Date: 4/9/13
 */
public class CategoryPriorCalculators {

	public static class BatchCategoryPriorCalculator implements ICategoryPriorCalculator {

		@Override
		public Double getPrior(INominalData data, INominalModel model, String categoryName) {
			if (data.arePriorsFixed())
				return data.getCategoryPrior(categoryName);
			else
				return model.getCategoryPriors().get(categoryName);
		}

		@Override
		public Map<String, Double> getPriors(INominalData data, INominalModel model) {
			if (data.arePriorsFixed())
				return data.getCategoryPriors();
			else
				return model.getCategoryPriors();
		}

		@Override
		public void initializeModelPriors(INominalData data, INominalModel model) {
			Map<String, Double> priors = new HashMap<String, Double>();
			for (String c : data.getCategories()){
				priors.put(c, 1. / data.getCategories().size());
			}
			model.setCategoryPriors(priors);
		}
	}

	public static class IncrementalCategoryPriorCalculator implements ICategoryPriorCalculator {

		@Override
		public Double getPrior(INominalData data, INominalModel model, String categoryName) {
			if (data.arePriorsFixed())
				return data.getCategoryPrior(categoryName);
			else if (((IIncrementalNominalModel) model).getPriorDenominator() == 0)
				return 1. / (double) data.getCategories().size();
			else
				return model.getCategoryPriors().get(categoryName) / ((IIncrementalNominalModel) model).getPriorDenominator();
		}

		@Override
		public Map<String, Double> getPriors(INominalData data, INominalModel model) {
			if (data.arePriorsFixed())
				return data.getCategoryPriors();
			else{
				Map<String, Double> ret = new HashMap<String, Double>();
				for (String cat : data.getCategories()){
					ret.put(cat, getPrior(data, model, cat));
				}
				return ret;
			}
		}

		@Override
		public void initializeModelPriors(INominalData data, INominalModel model) {
			Map<String, Double> priors = new HashMap<String, Double>();
			for (String c : data.getCategories()){
				priors.put(c, 0.);
			}
			model.setCategoryPriors(priors);
			((IIncrementalNominalModel)model).setPriorDenominator(0);
		}
	}

}
