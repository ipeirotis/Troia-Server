package com.datascience.scheduler;

import com.google.common.base.Objects;

import java.util.*;

/**
 * @Author: konrad
 */
public class IterablePriorityQueue<T> implements IIterablePriorityQueue<T> {


	protected NavigableSet<ElementWithPriority> queue;
	protected Map<T, ElementWithPriority> finder;
	protected Comparator<T> comparator;

	public IterablePriorityQueue(Comparator<T> comparator){
		this.comparator = comparator;
		clear();
	}

	@Override
	public void clear(){
		queue = new TreeSet<ElementWithPriority>();
		finder = new HashMap<T, ElementWithPriority>();
	}

	@Override
	public void addReplacing(T object, double priority) {
		remove(object);
		ElementWithPriority element = new ElementWithPriority(object, priority);
		finder.put(object, element);
		queue.add(element);
	}

	@Override
	public void remove(T object) {
		ElementWithPriority el = finder.get(object);
		if (el != null){
			queue.remove(el);
			finder.remove(object);
		}
	}

	@Override
	public T first() {
		if (queue.isEmpty()) return null;
		return queue.first().element;
	}

	@Override
	public T getAndRemoveFirst() {
		T el = first();
		remove(el);
		return el;
	}

	@Override
	public Iterator<T> iterator() {
		return new IPCIterator(queue.iterator());
	}

	protected class ElementWithPriority implements Comparable<ElementWithPriority>{

		protected T element;
		protected double priority;

		public ElementWithPriority(T element, double priority){
			this.element = element;
			this.priority = priority;
		}

		@Override
		public int compareTo(ElementWithPriority other) {
			int cmp = Double.compare(priority, other.priority);
			if (cmp == 0) {
				return comparator.compare(element, other.element);
			}
			return cmp;
		}

		@Override
		public boolean equals(Object object){
			if (object instanceof IterablePriorityQueue.ElementWithPriority){
				ElementWithPriority other = (IterablePriorityQueue.ElementWithPriority) object;
				return compareTo(other) == 0;
			}
			return false;
		}
	}

	protected class IPCIterator implements Iterator<T>{

		protected Iterator<ElementWithPriority> internalIterator;

		public IPCIterator(Iterator<ElementWithPriority> internalIterator){
			this.internalIterator = internalIterator;
		}

		@Override
		public boolean hasNext() {
			return internalIterator.hasNext();
		}

		@Override
		public T next() {
			return internalIterator.next().element;
		}

		@Override
		public void remove() {
			internalIterator.remove();
		}
	}
}
