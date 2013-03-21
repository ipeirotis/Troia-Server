package com.datascience.scheduler;

import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.nominal.decision.DecisionEngine;
import com.datascience.core.nominal.decision.ILabelProbabilityDistributionCostCalculator;

/**
 * I know that at some point we will be able to calculate cost for continuous project
 * but for now ...
 * @Author: konrad
 */
public class CostBasedPriorityCalculator implements IPriorityCalculator<String> {

	protected DecisionEngine dEngine;
	protected NominalProject project;

	public CostBasedPriorityCalculator(ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator){
		dEngine = new DecisionEngine(labelProbabilityDistributionCostCalculator, null);
	}

	@Override
	public double getPriority(LObject<String> object) {
		double val;
		if (project.getResults().hasDatumResult(object)){
			val = dEngine.estimateMissclassificationCost(project, object);
		} else {
			val = Double.MAX_VALUE;
		}
		return -val;
	}

	@Override
	public <V, W> void registerOnProject(Project<String, ?, V, W> project){
		if (!(project instanceof NominalProject)) {
			throw new IllegalArgumentException(this.getClass().toString() +
					" supports only NominalProjects, not " + project.getClass().toString());
		}
		this.project = (NominalProject) project;
	}

	@Override
	public String getId() {
		return "costbased";
	}

	@Override
	public <U extends Data<String>, V, W> ISchedulerNotificator<String> getSchedulerNotificator(Project<String, U, V, W> project) {
		return new SchedulerNewResultsNotificator<String, U, V>();
	}

}
