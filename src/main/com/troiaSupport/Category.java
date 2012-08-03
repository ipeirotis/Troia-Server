package main.com.troiaSupport;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;

/**
 * 
 * Class represents single label with misclassification costs map.
 * 
 * @author piotr.gnys@10clouds.com
 * 
 */
public class Category {

	/**
	 * Constructor that creates label only with name. Misclassification cost map
	 * is generated only with self reference valued as 0 cost.
	 * 
	 * @param name
	 *            Category name
	 */
	public Category(String name) {
		this(name, DEFAULT_PRIORITY, new HashMap<String, Double>());
	}

	/**
	 * Constructor that create category with given misclassification costs
	 * 
	 * @param name
	 *            Name of this category
	 * @param misclassificationCostsMap
	 *            Map that assigns misclassification costs to another categories
	 *            by their names.
	 */
	public Category(String name, Map<String, Double> misclassificationCostsMap) {
		this(name, DEFAULT_PRIORITY, misclassificationCostsMap);
	}

	/**
	 * Constructor that create category with given misclassification costs
	 * 
	 * @param name
	 *            Name of this category
	 * @param misclassificationCostsMap
	 *            Map that assigns misclassification costs to another categories
	 *            by their names.
	 */
	public Category(String name, double prior,
			Map<String, Double> misclassificationCostsMap) {
		super();
		this.name = name;
		this.prior = prior;
		this.misclassification_cost = misclassificationCostsMap;
	}

	/**
	 * @return Name of this category
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            Name of this category
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return Map that assigns misclassification costs to another categories by
	 *         their names.
	 */
	public Map<String, Double> getMisclassificationCostsMap() {
		return misclassification_cost;
	}

	/**
	 * @param misclassificationCostsMap
	 *            Map that assigns misclassification costs to another categories
	 *            by their names.
	 */
	public void setMisclassificationCostsMap(
			Map<String, Double> misclassificationCostsMap) {
		this.misclassification_cost = misclassificationCostsMap;
	}

	/**
	 * Sets misclassification cost for label given as a parameter
	 * 
	 * @param categoryName
	 *            Name of category for with misclassification cost will be set
	 * @param misclassificationCost
	 *            Misclassification cost for given label
	 */
	public void setMisclassificationCost(String categoryName,
			double misclassificationCost) {
		this.misclassification_cost.put(categoryName, misclassificationCost);
	}

	/**
	 * Returns misclassification cost for given label, if no cost was set
	 * default misclassification cost is returned.
	 * 
	 * @param categoryName
	 *            Name of category for with misclassification cost will be
	 *            returned
	 * @return Misclassification cost for given label or default
	 *         misclassification cost if none were set.
	 */
	public double getMisclassificationCost(String categoryName) {
		if (this.misclassification_cost.containsKey(categoryName)) {
			return this.misclassification_cost.get(categoryName);
		} else {
			return Category.DEFAULT_MISCLASSIFICATION_COST;
		}
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return new Gson().toJson(this).toString();
	}

	/**
	 * @return the prior
	 */
	public double getPrior() {
		return prior;
	}

	/**
	 * @param prior
	 *            the prior to set
	 */
	public void setPrior(double prior) {
		this.prior = prior;
	}

	/**
	 * Default misclassification cost for label that contains misclassification
	 * costs map. So in other words cost of situation in with classification was
	 * correct.
	 */
	public static double DEFAULT_CORRECT_CLASSIFICATION_COST = 0;

	/**
	 * Default misclassification cost for any label in misclassification costs
	 * map except one containing this map. In other words default cost of
	 * incorrect classification.
	 */
	public static double DEFAULT_MISCLASSIFICATION_COST = 1;

	/**
	 * Default priority
	 */
	public static double DEFAULT_PRIORITY = -1;

	/**
	 * Name of this label
	 */
	private String name;

	/**
	 * Label priority
	 */
	private double prior;

	/**
	 * Map that assigns misclassification costs to another labels by their
	 * names. Misclassification cost is value that informs us how serious damage
	 * comes from classifying object with this label instead of one in costs
	 * map.
	 */
	private Map<String, Double> misclassification_cost;

}
