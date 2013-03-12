package com.datascience.gal;

import com.datascience.core.nominal.NominalProject;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author konrad
 */
public class Quality {

	static public double fromCost(NominalProject project, double cost){
		return 1. - cost / project.getMinSpammerCost();
	}
	
	static public Map<String, Double> fromCosts(NominalProject project, Map<String, Double> costs){
		Map<String, Double> quality = new HashMap<String, Double>();
		for (Map.Entry<String, Double> e: costs.entrySet()) {
			quality.put(e.getKey(), fromCost(project, e.getValue()));
		}
		return quality;
	}
}
