package com.datascience.utils;

import com.datascience.core.nominal.NominalProject;

import java.util.Map;

/**
 * @Author: konrad
 */
public class MathHelpers {

	static public double getAverage(NominalProject project, Map<?, Double> costs){
		//sum of object qualities is: n - sum_of_costs/s, where s in minSpammerCost
		double cnt = costs.size();
		double costSum = 0.;
		for (Double val : costs.values()){
			costSum += val;
		}
		return costSum/cnt;
	}
}
