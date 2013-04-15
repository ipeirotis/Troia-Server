package com.datascience.scheduler;

import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;

public class AssignCountPriorityCalculator<T> implements IPriorityCalculator<T> {

	protected Data<T> data;

	public AssignCountPriorityCalculator() { }

	public AssignCountPriorityCalculator(Project<T, ?, ?, ?> project) {
		registerOnProject(project);
	}

	@Override
	public double getPriority(LObject<T> object) {
		return data.getAssignsForObject(object).size();
	}

	@Override
	public <V, W> void registerOnProject(Project<T, ?, V, W> project) {
		this.data = project.getData();
	}

	@Override
	public String getId(){
		return "countassigns";
	}

	@Override
	public <U extends Data<T>, V, W> ISchedulerNotificator<T> getSchedulerNotificator(Project<T, U, V, W> project) {
		return new SchedulerNewDataNotificator<T>();
	}
}
