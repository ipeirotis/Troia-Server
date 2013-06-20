package com.datascience.core.nominal.decision;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.datascience.core.base.LObject;
import com.datascience.core.nominal.NominalProject;
import com.datascience.utils.CostMatrix;

/**
 * Object of this class combines way of calculating labels probability distribution and choosing decision based
 * on this info. It also estimates cost that is related to this decision.
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
		return project.getObjectResults(datum).getCategoryProbabilites();
	}

	public String predictLabel(NominalProject project, LObject<String> datum, CostMatrix<String> cm) {
		return objectLabelDecisionAlgorithm.predictLabel(getPD(datum, project), cm);
	}

	public double estimateMissclassificationCost(NominalProject project, LObject<String> datum, CostMatrix<String> cm) {
		return labelProbabilityDistributionCostCalculator.predictedLabelCost(
			getPD(datum, project), cm);
	}

	public double estimateMissclassificationCost(NominalProject project, Map<String, Double> pd) {
		return labelProbabilityDistributionCostCalculator.predictedLabelCost(
				pd, project.getData().getCostMatrix());
	}

	public String predictLabel(NominalProject project, LObject<String> datum) {
		return predictLabel(project, datum, project.getData().getCostMatrix());
	}

	public double estimateMissclassificationCost(NominalProject project, LObject<String> datum) {
		return estimateMissclassificationCost(project, datum, project.getData().getCostMatrix());
	}

	public Map<LObject<String>, String> predictLabels(NominalProject project){
		Collection<LObject<String>> datums = project.getData().getObjects();
		CostMatrix<String> cm = project.getData().getCostMatrix();
		Map<LObject<String>, String> ret = new HashMap<LObject<String>, String>();
		for (LObject<String> e: datums) {
			ret.put(e, predictLabel(project, e, cm));
		}
		return ret;
	}

	public Map<LObject<String>, Double> estimateMissclassificationCosts(NominalProject project){
		Collection<LObject<String>> datums = project.getData().getObjects();
		CostMatrix<String> cm = project.getData().getCostMatrix();
		Map<LObject<String>, Double> ret = new HashMap<LObject<String>, Double>();
		for (LObject<String> e: datums) {
			ret.put(e, estimateMissclassificationCost(project, e, cm));
		}
		return ret;
	}
}
