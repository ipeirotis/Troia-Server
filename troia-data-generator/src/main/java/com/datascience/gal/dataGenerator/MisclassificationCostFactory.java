package com.datascience.gal.dataGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import com.datascience.core.stats.Category;
import com.datascience.gal.MisclassificationCost;

/**
 * It's used to create MisclassificationCost basing on input of Category collection. Category
 * object in input must contain their misclassification maps in correct format.
 *
 * @author piotr.gnys@10clouds.com
 */
public class MisclassificationCostFactory {




	public ArrayList<MisclassificationCost> getMisclassificationCosts(Collection<Category> categories) {
		ArrayList<MisclassificationCost> costs = new ArrayList<MisclassificationCost>();
		for (Category category : categories) {
			Map<String,Double> costMap = category.getMisclassificationCosts();
			Collection<String> keys = costMap.keySet();
			for (String key : keys) {
				costs.add(new MisclassificationCost(category.getName(),key,costMap.get(key)));
			}
		}
		return costs;
	}

	/**
	 * @return Instance of MissclassificationCostFactory
	 */
	public static MisclassificationCostFactory getInstance() {
		return instance;
	}

	/**
	 * Constructor is private so only one object of this class will exist
	 */
	private MisclassificationCostFactory() {

	}

	/**
	 * Singleton of MissclassificationCostFactory
	 */
	private static final MisclassificationCostFactory instance = new MisclassificationCostFactory();

}
