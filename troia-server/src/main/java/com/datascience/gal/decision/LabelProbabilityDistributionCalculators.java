package com.datascience.gal.decision;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.gal.Category;
import com.datascience.gal.NominalProject;
import com.google.common.base.Strings;

/**
 * @author Konrad Kurdej
 */
public class LabelProbabilityDistributionCalculators {
	// This classes should be in proper classes like MajorityVote, Dawid Skene etc.

	public static class DS implements ILabelProbabilityDistributionCalculator {

		@Override
		public Map<String, Double> calculateDistribution(LObject<String> datum,	NominalProject project) {
			if (datum.isGold()) {
				return Utils.generateGoldDistribution(project.getData().getCategoriesNames(), datum.getGoldLabel());
			}
			return project.getResults().getDatumResults().get(datum).getCategoryProbabilites();
		}
	}

	public static class MV implements ILabelProbabilityDistributionCalculator {

		@Override
		public Map<String, Double> calculateDistribution(LObject<String> datum, NominalProject project) {
			if (datum.isGold()) {
				return Utils.generateGoldDistribution(project.getData().getCategoriesNames(), datum.getGoldLabel());
			}
			Map<String, Double> pd = new HashMap<String, Double>();
			for (Category c: project.getData().getCategories()) {
				pd.put(c.getName(), 0.0);
			}

			Collection<AssignedLabel<String>> assignedLabels = project.getData().getAssignsForObject(datum);
			double revn = 1. / assignedLabels.size();
			for (AssignedLabel<String> al : assignedLabels) {
				String c = al.getLabel();
				Double current = pd.get(c);
				pd.put(c, current + revn);
			}
			return pd;
		}
	}
	
	public static class PriorBased implements ILabelProbabilityDistributionCalculator {

		@Override
		public Map<String, Double> calculateDistribution(LObject<String> datum, NominalProject project) {
			Map<String, Double> pd = new HashMap<String, Double>();
			for (Category c: project.getData().getCategories()) {
				pd.put(c.getName(), c.getPrior());
			}
			return pd;
		}
	}
	
	public static class OnlyOneLabel implements ILabelProbabilityDistributionCalculator {
		
		private DecisionEngine decisionEngine;
		
		public OnlyOneLabel(DecisionEngine decisionEngine) {
			this.decisionEngine = decisionEngine;
		}

		@Override
		public Map<String, Double> calculateDistribution(LObject<String> datum, NominalProject project) {
			String label = decisionEngine.predictLabel(project, datum);
			Map<String, Double> pd = new HashMap<String, Double>();
			for (Category c: project.getData().getCategories()) {
				pd.put(c.getName(), 0.);
			}
			pd.put(label, 1.);
			return pd;
		}
	}
	
	public static ILabelProbabilityDistributionCalculator get(String algorithm){
		if (Strings.isNullOrEmpty(algorithm)) {
			algorithm = "DS";
		}
		algorithm = algorithm.toUpperCase();
		if ("DS".equals(algorithm)) {
			return new DS();
		}
		if ("MV".equals(algorithm)) {
			return new MV();
		}
		if ("SPAMMER".equals(algorithm) || "NOVOTE".equals(algorithm)) {
			return new PriorBased();
		}
		throw new IllegalArgumentException("Not known label probability distrinbution calculator: " + algorithm);
	}
}
