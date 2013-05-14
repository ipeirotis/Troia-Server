package com.datascience.scheduler;

import com.datascience.core.base.IData;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;

public class AssignCountPriorityCalculator<T> implements IPriorityCalculator<T> {

	protected IData<T> data;

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
	public <U extends IData<T>, V, W> ISchedulerNotificator<T> getSchedulerNotificator(Project<T, U, V, W> project) {
		return new SchedulerNewDataNotificator<T>();
	}
}
