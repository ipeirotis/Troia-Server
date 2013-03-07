package com.datascience.scheduler;


import com.datascience.core.base.ContValue;
import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.galc.ContinuousProject;

public class ContPriorityCalculator implements IPriorityCalculator<ContValue> {

	private Data<ContValue> data;

	public ContPriorityCalculator(ContinuousProject project) {
		data = project.getData();
	}

	@Override
	public double getPriority(LObject<ContValue> object) {
		return data.getAssignsForObject(object).size();
	}
}
