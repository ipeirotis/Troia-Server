package com.datascience.gal.commands;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.Datum;

/**
 *
 * @author konrad
 */
public class ParamChecking {
	
	public static Datum datum(AbstractDawidSkene ads, String datumId){
		Datum d = ads.getObject(datumId);
		if (d == null) {
			throw new IllegalArgumentException("No datum with id: " + datumId);
		}
		return d;
	}
}
