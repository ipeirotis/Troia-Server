package com.datascience.core.stats;

import com.datascience.core.nominal.IncrementalNominalModel;
import com.datascience.core.nominal.NominalData;
import com.datascience.core.nominal.NominalModel;

/**
 * User: artur
 * Date: 4/9/13
 */
public class CategoryPriorCalculators {

	public static class BatchCategoryPriorCalculator implements ICategoryPriorCalculator {

		@Override
		public Double getPrior(NominalData data, NominalModel model, String categoryName) {
			if (data.arePriorsFixed())
				return data.getCategoryPrior(categoryName);
			else
				return model.categoryPriors.get(categoryName);
		}

		@Override
		public void initializeModelPriors(NominalData data, NominalModel model) {
			for (String c : data.getCategories()){
				model.categoryPriors.put(c, 1. / data.getCategories().size());
			}
		}
	}

	public static class IncrementalCategoryPriorCalculator implements ICategoryPriorCalculator {

		@Override
		public Double getPrior(NominalData data, NominalModel model, String categoryName) {
			if (data.arePriorsFixed())
				return data.getCategoryPrior(categoryName);
			else if (((IncrementalNominalModel) model).priorDenominator == 0)
				return 1. / (double) data.getCategories().size();
			else
				return model.categoryPriors.get(categoryName) / ((IncrementalNominalModel) model).priorDenominator;
		}

		@Override
		public void initializeModelPriors(NominalData data, NominalModel model) {
			for (String c : data.getCategories()){
				model.categoryPriors.put(c, 0.);
			}
			((IncrementalNominalModel)model).priorDenominator = 0;
		}
	}

}
