package com.datascience.galc;

import java.util.Set;
import java.util.TreeSet;

public class DatumCont implements Comparable<DatumCont> {

	private String							name;
	private Set<AssignedLabel>	labels;

	private Boolean							isGold;
	private Double							goldValue;
	private Double							goldZeta;

	
	private Double							est_value;
	private Double							est_zeta;

	// Data generation characteristics
	private Double							trueValue;
	private Double							trueZeta;

	public DatumCont(String name) {

		this.name = name;
		this.isGold = false;
		this.labels = new TreeSet<AssignedLabel>();

	}

	public void addAssignedLabel(AssignedLabel al) {

		if (al.getDatum().equals(name)) {
			this.labels.add(al);
		}
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
	}

	/**
	 * @return the est_value
	 */
	public Double getEst_value() {
	
		return est_value;
	}

	
	/**
	 * @param est_value the est_value to set
	 */
	public void setEst_value(Double est_value) {
	
		this.est_value = est_value;
	}

	
	/**
	 * @return the est_zeta
	 */
	public Double getEst_zeta() {
	
		return est_zeta;
	}

	
	/**
	 * @param est_zeta the est_zeta to set
	 */
	public void setEst_zeta(Double est_zeta) {
	
		this.est_zeta = est_zeta;
	}

	
	/**
	 * @return the trueZeta
	 */
	public Double getTrueZeta() {
	
		return trueZeta;
	}

	
	/**
	 * @param trueZeta the trueZeta to set
	 */
	public void setTrueZeta(Double trueZeta) {
	
		this.trueZeta = trueZeta;
	}

	public Double getTrueValue() {

		return trueValue;
	}

	public void setTrueValue(Double trueValue) {

		this.trueValue = trueValue;
	}
	

	public Boolean isGold() {
		return isGold;
	}


	public void setGold(Boolean isGold) {
		this.isGold = isGold;
	}


	public Double getGoldValue() {
		return goldValue;
	}

	public void setGoldValue(Double goldValue) {
		this.goldValue = goldValue;
	}

	public Double getGoldZeta() {
		return goldZeta;
	}

	public void setGoldZeta(Double goldZeta) {
		this.goldZeta = goldZeta;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof DatumCont))
			return false;
		DatumCont other = (DatumCont) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	/**
	 * @return the labels
	 */
	public Set<AssignedLabel> getAssignedLabels() {

		return labels;
	}

	/**
	 * @param labels
	 *          the labels to set
	 */
	public void setLabels(Set<AssignedLabel> labels) {

		this.labels = labels;
	}

	@Override
	public int compareTo(DatumCont o) {

		return this.getName().compareTo(o.getName());
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		return "DatumCont [name=" + name + ", est_value=" + est_value + ", est_zeta=" + est_zeta + ", trueValue="
				+ trueValue + ", trueZeta=" + trueZeta + "]";
	}


}
