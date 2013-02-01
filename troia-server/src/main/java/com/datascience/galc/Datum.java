package com.datascience.galc;

import java.util.Set;
import java.util.HashSet;

import com.google.common.base.Objects;

public class Datum {

	private String							name;
	private Set<AssignedLabel>				labels;
	
	public Datum(String name) {

		this.name = name;
		this.labels = new HashSet<AssignedLabel>();

	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	public void addAssignedLabel(AssignedLabel al) {

		if (al.getObjectName().equals(name)) {
			this.labels.add(al);
		}
	}

	/**
	 * @return the labels
	 */
	public Set<AssignedLabel> getAssignedLabels() {

		return labels;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return Objects.hashCode( this.name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Datum))
			return false;
		Datum other = (Datum) obj;
		return Objects.equal(name, other.name);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			       .add("name", name)
			       .toString();
	}

}
