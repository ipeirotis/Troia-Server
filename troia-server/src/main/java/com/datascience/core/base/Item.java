package com.datascience.core.base;

import com.google.common.base.Objects;

/**
 * @Author: konrad
 */
public class Item {

	protected String name;

	public Item(String name){
		this.name = name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getName(){
		return name;
	}

	@Override
	public int hashCode(){
		return Objects.hashCode(name);
	}

	@Override
	public boolean equals(Object other){
		if (other instanceof Item) {
			return Objects.equal(name, ((Item) other).name);
		}
		return false;
	}
}
