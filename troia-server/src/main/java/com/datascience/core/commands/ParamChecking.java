package com.datascience.core.commands;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;

/**
 *
 * @author konrad
 */
public class ParamChecking {
	
	public static <T> LObject<T> datum(Project project, String datumId){
		LObject<T> d = project.getData().getObject(datumId);
		if (d == null) {
			throw new IllegalArgumentException("No datum with id: " + datumId);
		}
		return d;
	}
}
