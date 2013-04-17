package com.datascience.core.commands;

import com.datascience.core.base.AssignedLabel;

/**
 * @Author: konrad
 */
public class Utils {

	public static class ShallowAssign<T>{

		public String worker;
		public String object;
		public T label;

		public ShallowAssign(AssignedLabel<T> assign){
			worker = assign.getWorker().getName();
			object = assign.getLobject().getName();
			label = assign.getLabel();
		}
	}
}
