package com.datascience.galc;

import java.util.Set;
import java.util.HashSet;


public abstract class Data {

	Set<DatumCont>			objects	= new HashSet<DatumCont>();
	Set<WorkerCont>					workers	= new HashSet<WorkerCont>();
	Set<AssignedLabel>	labels	= new HashSet<AssignedLabel>();

	public Set<DatumCont> getObjects() {

		return objects;
	}

	public Set<WorkerCont> getWorkers() {

		return workers;
	}

	public Set<AssignedLabel> getLabels() {

		return labels;
	}
}
