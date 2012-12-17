package com.datascience.gal.dawidSkeneProcessors;

import com.datascience.gal.DawidSkene;

/*
 * Interface which is used to represent and encapsulate all the information needed to call a method on DawidSkene
 */
public interface DawidSkeneCommand {
	void execute(DawidSkene ds);
	
}
