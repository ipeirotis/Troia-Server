package com.datascience.galc;

import com.google.common.base.Objects;

public class AssignedLabel implements Comparable<AssignedLabel> {

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
		return com.google.common.base.Objects.hashCode( this.label, this.object_id, this.worker_id); 
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null)
			return false;
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

	@Override
	public int compareTo(AssignedLabel o) {

		int c1 = this.getDatum().compareTo(o.getDatum());
		int c2 = this.getWorker().compareTo(o.getWorker());

		return (c1 == 0) ? c2 : c1;

	}

}
