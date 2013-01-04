package com.datascience.gal.decision;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.datascience.gal.AssignedLabel;
import com.datascience.gal.Category;
import com.datascience.gal.Datum;
import com.datascience.gal.DawidSkene;
import com.google.common.base.Strings;

/**
 * @author Konrad Kurdej
 */
public class LabelProbabilityDistributionCalculators {
	// This classes should be in proper classes like MajorityVote, Dawid Skene etc.

	public static class DS implements ILabelProbabilityDistributionCalculator {

		@Override
		public Map<String, Double> calculateDistribution(Datum datum,
				DawidSkene ads) {
			if (datum.isGold()) {
				return Utils.generateGoldDistribution(ads.getCategories().keySet(), datum.getCorrectCategory());
			}
			return datum.getCategoryProbability();
		}
	}

	public static class MV implements ILabelProbabilityDistributionCalculator {

		@Override
		public Map<String, Double> calculateDistribution(Datum datum,
				DawidSkene ads) {
			if (datum.isGold()) {
				return Utils.generateGoldDistribution(ads.getCategories().keySet(), datum.getCorrectCategory());
			}
			Map<String, Double> pd = new HashMap<String, Double>();
			for (Category c: ads.getCategories().values()) {
				pd.put(c.getName(), 0.0);
			}

			Collection<AssignedLabel> assignedLabels = datum.getAssignedLabels();
			double revn = 1. / assignedLabels.size();
			for (AssignedLabel al : assignedLabels) {
				String c = al.getCategoryName();
				Double current = pd.get(c);
				pd.put(c, current + revn);
			}
			return pd;
		}
	}
	
	public static class PriorBased implements ILabelProbabilityDistributionCalculator {

		@Override
		public Map<String, Double> calculateDistribution(Datum datum,
				DawidSkene ads) {
			Map<String, Double> pd = new HashMap<String, Double>();
			for (Category c: ads.getCategories().values()) {
				pd.put(c.getName(), c.getPrior());
			}
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
		if ("SPAMMER".equals(algorithm)) {
			return new PriorBased();
		}
		throw new IllegalArgumentException("Not known label probability distrinbution calculator: " + algorithm);
	}
}
