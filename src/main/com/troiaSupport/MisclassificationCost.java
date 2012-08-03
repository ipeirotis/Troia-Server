package main.com.troiaSupport;

/**
 * Representation of single misclassification cost between two categories recognised
 * by names.
 *
 */
public class MisclassificationCost {

	
	
	
	/**
	 * @param categoryFrom Category that should have been assigned to object
	 * @param cost Cost of misclassification.
	 * @param categoryTo Category that was assigned to object
	 */
	public MisclassificationCost(String categoryFrom, String categoryTo,
			double cost) {
		super();
		this.categoryFrom = categoryFrom;
		this.categoryTo = categoryTo;
		this.cost = cost;
	}

	
	
	
	/**
	 * @return Category that should have been assigned to object
	 */
	public String getCategoryFrom() {
		return categoryFrom;
	}

	/**
	 * @param categoryFrom Category that should have been assigned to object
	 */
	public void setCategoryFrom(String categoryFrom) {
		this.categoryFrom = categoryFrom;
	}

	/**
	 * @return Category that was assigned to object
	 */
	public String getCategoryTo() {
		return categoryTo;
	}

	/**
	 * @param categoryTo Category that was assigned to object
	 */
	public void setCategoryTo(String categoryTo) {
		this.categoryTo = categoryTo;
	}

	/**
	 * @return Cost of misclassification.
	 */
	public double getCost() {
		return cost;
	}

	/**
	 * @param cost Cost of misclassification.
	 */
	public void setCost(double cost) {
		this.cost = cost;
	}




	/**
	 * Category that should have been assigned to object
	 */
	private String categoryFrom;
	
	/**
	 * Category that was assigned to object
	 */
    private String categoryTo;
    
    /**
     * Cost of misclassification.
     */
    private double cost;
}
