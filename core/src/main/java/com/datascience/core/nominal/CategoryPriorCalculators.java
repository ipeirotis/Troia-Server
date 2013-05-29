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
		public Double getPrior(INominalData data, NominalModel model, String categoryName) {
			if (data.arePriorsFixed())
				return data.getCategoryPrior(categoryName);
			else
				return model.categoryPriors.get(categoryName);
		}

		@Override
		public Map<String, Double> getPriors(INominalData data, NominalModel model){
			if (data.arePriorsFixed())
				return data.getCategoryPriors();
			else
				return model.categoryPriors;
		}

		@Override
		public void initializeModelPriors(INominalData data, NominalModel model) {
			for (String c : data.getCategories()){
				model.categoryPriors.put(c, 1. / data.getCategories().size());
			}
		}
	}

	public static class IncrementalCategoryPriorCalculator implements ICategoryPriorCalculator {

		@Override
		public Double getPrior(INominalData data, NominalModel model, String categoryName) {
			if (data.arePriorsFixed())
				return data.getCategoryPrior(categoryName);
			else if (((IncrementalNominalModel) model).priorDenominator == 0)
				return 1. / (double) data.getCategories().size();
			else
				return model.categoryPriors.get(categoryName) / ((IncrementalNominalModel) model).priorDenominator;
		}

		@Override
		public Map<String, Double> getPriors(INominalData data, NominalModel model){
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
		public void initializeModelPriors(INominalData data, NominalModel model) {
			for (String c : data.getCategories()){
				model.categoryPriors.put(c, 0.);
			}
			((IncrementalNominalModel)model).priorDenominator = 0;
		}
	}

}
