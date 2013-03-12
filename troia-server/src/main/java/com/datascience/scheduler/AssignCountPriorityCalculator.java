package com.datascience.scheduler;

import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;

public class AssignCountPriorityCalculator<T> implements IPriorityCalculator<T> {

	private Data<T> data;

	public AssignCountPriorityCalculator() { }

	public AssignCountPriorityCalculator(Project<T, ?, ?, ?> project) {
		setProject(project);
		data = project.getData();
	}

	@Override
	public double getPriority(LObject<T> object) {
		return data.getAssignsForObject(object).size();
	}

	@Override
	public void setProject(Project<T, ?, ?, ?> project) {
		this.data = project.getData();
	}
}
