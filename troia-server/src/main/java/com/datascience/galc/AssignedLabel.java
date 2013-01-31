package com.datascience.galc;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

public class AssignedLabel {

	private String	worker_name;
	private String	object_name;
	private Double	label;

	public AssignedLabel(String w, String o, Double label) {

		this.worker_name = w;
		this.object_name = o;
		this.label = label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode( this.label, this.object_name, this.worker_name); 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof AssignedLabel))
			return false;
		AssignedLabel other = (AssignedLabel) obj;
		return Objects.equal(this.label, other.label) 
			&& Objects.equal(this.object_name, other.object_name)
			&& Objects.equal(this.worker_name, other.worker_name);
	}

	/**
	 * @return the workerName
	 */
	public String getWorkerName() {

		return worker_name;
	}

	/**
	 * @return the objectName
	 */
	public String getObjectName() {

		return object_name;
	}

	/**
	 * @return the label
	 */
	public Double getLabel() {

		return label;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			       .add("worker_name", worker_name)
			       .add("object_name", object_name)
			       .add("label_value", label)
			       .toString();	
		}
}
