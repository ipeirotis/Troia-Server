package com.datascience.galc.dataGenerator;

import java.util.Set;
import java.util.HashSet;

import com.datascience.galc.AssignedLabel;
import com.datascience.galc.DatumCont;
import com.datascience.galc.Worker;

public abstract class Data {

	Set<DatumCont>			objects	= new HashSet<DatumCont>();
	Set<Worker>					workers	= new HashSet<Worker>();
	Set<AssignedLabel>	labels	= new HashSet<AssignedLabel>();

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
