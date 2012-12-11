package com.datascience.galc;

import java.util.Set;
import java.util.TreeSet;


public abstract class Data {

	Set<DatumCont>			objects	= new TreeSet<DatumCont>();
	Set<Worker>					workers	= new TreeSet<Worker>();
	Set<AssignedLabel>	labels	= new TreeSet<AssignedLabel>();

	
	public Set<DatumCont> getObjects() {

		return objects;
	}

	public Set<Worker> getWorkers() {

		return workers;
	}

	public Set<AssignedLabel> getLabels() {

		return labels;
	}
}
