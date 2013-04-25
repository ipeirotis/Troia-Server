package com.datascience.gal.dataGenerator;

import com.google.common.base.Objects;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is created because during tests we usually want to know correct
 * classes for all object not only for ones with gold labels. This class
 * implements Collection interface and when treated as such it is simply a
 * collection of class names. Thanks to that it can be used in places where
 * normally collection of object names is sent for labeling. However this class
 * also provides functionality that allows you to 'cheat' and ask for correct
 * label for each object. This is useful if you want to test how close to
 * reality are result of DSaS algorithm.
 *
 * @author piotr.gnys@10clouds.com
 */
public class TroiaObjectCollection implements Collection<String> {

	/**
	 * Constructor that creates empty test objects collection
	 */
	public TroiaObjectCollection() {
		this(new HashMap<String, String>());
	}

	/**
	 * @param testObject
	 *            Map that holds pairs of {name,category} where name is key and
	 *            category value.
	 */
	public TroiaObjectCollection(Map<String, String> testObject) {
		super();
		this.testObject = testObject;
	}

	/**
	 * @see java.util.Collection#add(java.lang.Object)
	 */

	public boolean add(String objectName) {
		if (this.testObject.containsKey(objectName)) {
			return false;
		} else {
			this.testObject.put(objectName, null);
			return true;
		}
	}

	public boolean add(String objectName, String category) {
		if (this.testObject.containsKey(objectName)) {
			return false;
		} else {
			this.testObject.put(objectName, category);
			return true;
		}
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */

	public boolean addAll(Collection<? extends String> objects) {
		boolean retVal = false;
		for (String string : objects) {
			if (this.add(string)) {
				retVal = true;
			}
		}
		return retVal;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Collection#clear()
	 */

	public void clear() {
		this.testObject.clear();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Collection#contains(java.lang.Object)
	 */

	public boolean contains(Object o) {
		return this.testObject.containsKey(o);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */

	public boolean containsAll(Collection<?> c) {
		for (Object object : c) {
			if (!this.testObject.containsKey(object))
				return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Collection#isEmpty()
	 */

	public boolean isEmpty() {
		return this.testObject.isEmpty();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Collection#iterator()
	 */

	public Iterator<String> iterator() {
		return this.testObject.keySet().iterator();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Collection#remove(java.lang.Object)
	 */

	public boolean remove(Object o) {
		return this.testObject.remove(o) != null;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */

	public boolean removeAll(Collection<?> c) {
		boolean retVal = false;
		for (Object object : c) {
			if (this.remove(object)) {
				retVal = true;
			}
		}
		return retVal;
	}

	/**
	 * (non-Javadoc)
	 *
	 * @see java.util.Collection#retainAll(java.util.Collection) THIS DO NOT
	 *      WORK
	 */

	public boolean retainAll(Collection<?> c) {
		return false;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Collection#size()
	 */

	public int size() {
		return this.testObject.size();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Collection#toArray()
	 */

	public Object[] toArray() {
		return this.testObject.keySet().toArray();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see java.util.Collection#toArray(T[])
	 */

	public <T> T[] toArray(T[] a) {
		return this.testObject.keySet().toArray(a);
	}

	/**
	 * @param objectName
	 *            Name of object for with category is returned
	 * @return Category of given object
	 */
	public String getCategory(String objectName) {
		return this.testObject.get(objectName);
	}

	/**
	 * Adds object with given name and category, or changes object category if
	 * it already exists.
	 *
	 * @param objectName
	 *            Name of object for with category is set
	 * @param category
	 *            Name of category
	 */
	public void setCategory(String objectName, String category) {
		this.testObject.put(objectName, category);
	}

	/**
	 * Generates multiple lines string, each line format is as follow <object
	 * name><tab><category>
	 *
	 * @return List of object, category pairs in easy to read format
	 * @see java.lang.Object#toString()
	 */

	public String toString() {
		StringBuffer sb = new StringBuffer();
		Collection<String> objects = this.testObject.keySet();
		for (String object : objects) {
			sb.append(object);
			sb.append("\t");
			sb.append(getCategory(object));
			sb.append("\n");
		}
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof TroiaObjectCollection)) {
			return false;
		}
		TroiaObjectCollection c = (TroiaObjectCollection) o;
		return testObject.equals(c.testObject);
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(testObject);
	}

	/**
	 * Map that holds pairs of {name,category} where name is key and category
	 * value.
	 */
	Map<String, String> testObject;

}
