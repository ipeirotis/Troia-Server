package com.datascience.utils.objects;

import java.util.Set;
import java.util.HashSet;

import com.datascience.galc.AssignedLabel;
import com.google.common.base.Objects;

public class Worker {

	private String							name;

	private Set<AssignedLabel>				labels;

	public Worker(String name) {

		this.name = name;
		this.labels = new HashSet<AssignedLabel>();
	}

	public void addAssignedLabel(AssignedLabel al) {

		if (al.getWorkerName().equals(name)) {
			this.labels.add(al);
		}
	}

	/**
	 * @return the labels
	 */
	public Set<AssignedLabel> getLabels() {

		return labels;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode( this.name);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Worker))
			return false;
		Worker other = (Worker) obj;
		return Objects.equal(this.name, other.name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			       .add("name", name)
			       .toString();
	}


}
