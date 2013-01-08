package com.datascience.gal;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author konrad
 */
public class Quality {

	static public double fromCost(DawidSkene ds, double cost){
		return 1. - cost / ((AbstractDawidSkene) ds).getMinSpammerCost();
	}
	
	static public Map<String, Double> fromCosts(DawidSkene ds, Map<String, Double> costs){
		Map<String, Double> quality = new HashMap<String, Double>();
		for (Map.Entry<String, Double> e: costs.entrySet()) {
			quality.put(e.getKey(), fromCost(ds, e.getValue()));
		}
		return quality;
	}
}
