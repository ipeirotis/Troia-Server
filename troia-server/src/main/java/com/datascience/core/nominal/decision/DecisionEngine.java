package com.datascience.core.nominal.decision;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.datascience.core.base.LObject;
import com.datascience.core.nominal.NominalProject;
import com.datascience.utils.CostMatrix;
import com.datascience.utils.ProbabilityDistributions;

/**
 *
 * @author konrad
 */
public class DecisionEngine {

	ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator;
	IObjectLabelDecisionAlgorithm objectLabelDecisionAlgorithm;

	public DecisionEngine(ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator,
			IObjectLabelDecisionAlgorithm objectLabelDecisionAlgorithm){
		this.labelProbabilityDistributionCostCalculator = labelProbabilityDistributionCostCalculator;
		this.objectLabelDecisionAlgorithm = objectLabelDecisionAlgorithm;
	}


	public Map<String, Double> getPD(LObject<String> datum, NominalProject project){
		return project.getAlgorithm().calculateDistribution(datum);
	}

	public String predictLabel(NominalProject project, LObject<String> datum, CostMatrix<String> cm) {
		return objectLabelDecisionAlgorithm.predictLabel(getPD(datum, project), cm);
	}

	public double estimateMissclassificationCost(NominalProject project, LObject<String> datum, CostMatrix<String> cm) {
		return labelProbabilityDistributionCostCalculator.predictedLabelCost(
			getPD(datum, project), cm);
	}

	public String predictLabel(NominalProject project, LObject<String> datum) {
		return predictLabel(project, datum, ProbabilityDistributions.getCategoriesCostMatrix(project));
	}

	public double estimateMissclassificationCost(NominalProject project, LObject<String> datum) {
		return estimateMissclassificationCost(project, datum, ProbabilityDistributions.getCategoriesCostMatrix(project));
	}

	public Map<String, String> predictLabels(NominalProject project){
		Collection<LObject<String>> datums = project.getData().getObjects();
		CostMatrix<String> cm = ProbabilityDistributions.getCategoriesCostMatrix(project);
		Map<String, String> ret = new HashMap<String, String>();
		for (LObject<String> e: datums) {
			ret.put(e.getName(), predictLabel(project, e, cm));
		}
		return ret;
	}

	public Map<String, Double> estimateMissclassificationCosts(NominalProject project){
		Collection<LObject<String>> datums = project.getData().getObjects();
		CostMatrix<String> cm = ProbabilityDistributions.getCategoriesCostMatrix(project);
		Map<String, Double> ret = new HashMap<String, Double>();
		for (LObject<String> e: datums) {
			ret.put(e.getName(), estimateMissclassificationCost(project, e, cm));
		}
		return ret;
	}
}
