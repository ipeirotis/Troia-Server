package main.com.troiaSupport;

public class Label {

	
	
	
	
	
	
	/**
	 * @param workerName Name of worker that assigned this label
	 * @param objectName Name of object to with this label was assigned
	 * @param categoryName Name of category that was assigned by this label
	 */
	public Label(String workerName, String objectName, String categoryName) {
		super();
		this.workerName = workerName;
		this.objectName = objectName;
		this.categoryName = categoryName;
	}

	/**
	 * @return Name of worker that assigned this label
	 */
	public String getWorkerName() {
		return workerName;
	}

	/**
	 * @param workerName Name of worker that assigned this label
	 */
	public void setWorkerName(String workerName) {
		this.workerName = workerName;
	}

	/**
	 * @return Name of object to with this label was assigned
	 */
	public String getObjectName() {
		return objectName;
	}

	/**
	 * @param objectName Name of object to with this label was assigned
	 */
	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	/**
	 * @return Name of category that was assigned by this label
	 */
	public String getCategoryName() {
		return categoryName;
	}

	/**
	 * @param categoryName Name of category that was assigned by this label
	 */
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	
	

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Label [worker name=" + getWorkerName()
				+ ", object name=" + getObjectName()
				+ ", category name=" + getCategoryName() + "]";
	}



	/**
	 * Name of worker that assigned this label
	 */
	private String workerName;
	
	/**
	 * Name of object to with this label was assigned
	 */
	private String objectName;
	
	/**
	 * Name of category that was assigned by this label
	 */
	private String categoryName;
}
