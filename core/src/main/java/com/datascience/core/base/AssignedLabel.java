package com.datascience.core.base;

import com.google.common.base.Objects;

/**
 * We ignore label in comparison/hashcode
 *
 * @Author: konrad
 */
public class AssignedLabel<T> {

	protected LObject<T> lobject;
	protected Worker<T> worker;
	protected T label;
	
	public AssignedLabel(Worker<T> worker, LObject<T> object, T label){
		this.label = label;
		this.lobject = object;
		this.worker = worker;
	}

	public LObject<T> getLobject() {
		return lobject;
	}

	public void setLobject(LObject lobject) {
		this.lobject = lobject;
	}

	public T getLabel() {
		return label;
	}

	public void setLabel(T label) {
		this.label = label;
	}

	public Worker<T> getWorker() {
		return worker;
	}

	public void setWorker(Worker<T> worker) {
		this.worker = worker;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof AssignedLabel) {
			AssignedLabel al = (AssignedLabel) o;
			return Objects.equal(lobject, al.lobject) &&
					Objects.equal(worker, al.worker);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(lobject, worker);
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
				.add("lobject", (lobject != null)?lobject.getName():"-")
				.add("worker", (worker != null)?worker.getName():"-")
				.add("label", label)
				.toString();
	}
}
