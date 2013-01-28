package com.datascience.galc;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

public class AssignedLabel {

	private String	worker_id;
	private String	object_id;
	private Double	label;

	public AssignedLabel(String wid, String oid, Double label) {

		this.worker_id = wid;
		this.object_id = oid;
		this.label = label;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode( this.label, this.object_id, this.worker_id); 
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
			&& Objects.equal(this.object_id, other.object_id)
			&& Objects.equal(this.worker_id, other.worker_id);
	}

	/**
	 * @return the worker_id
	 */
	public String getWorker() {

		return worker_id;
	}

	/**
	 * @param worker_id
	 *          the worker_id to set
	 */
	public void setWorker(String worker_id) {

		this.worker_id = worker_id;
	}

	/**
	 * @return the object_id
	 */
	public String getDatum() {

		return object_id;
	}

	/**
	 * @param object_id
	 *          the object_id to set
	 */
	public void setDatum(String object_id) {

		this.object_id = object_id;
	}

	/**
	 * @return the label
	 */
	public Double getLabel() {

		return label;
	}

	/**
	 * @param label
	 *          the label to set
	 */
	public void setLabel(Double label) {

		this.label = label;
	}

	public int compareTo(AssignedLabel o) {

		return ComparisonChain.start()
		         .compare(this.getDatum(), o.getDatum())
		         .compare(this.getWorker(), o.getWorker())
		         .result();
	}

}
