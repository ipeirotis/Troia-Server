package com.datascience.core.base;

import com.google.common.base.Objects;

/**
 * We ignore label in comparison/hashcode
 *
 * @Author: konrad
 */
public class AssignedLabel<T> {

	protected Item item;
	protected Worker<T> worker;
	protected Label<T> label;


	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public Label getLabel() {
		return label;
	}

	public void setLabel(Label<T> label) {
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
			return Objects.equal(item, al.item) &&
					Objects.equal(worker, al.worker);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(item, worker);
	}
}
