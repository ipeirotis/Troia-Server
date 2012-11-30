package com.datascience.core;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.BatchDawidSkene;
import com.datascience.gal.IncrementalDawidSkene;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Konrad Kurdej
 */
public class JobFactory {

	private interface Creator{
		AbstractDawidSkene create(String id);
	}
	
	final static Map<String, Creator> DS_FACTORY = new HashMap();
	{
		DS_FACTORY.put("batch", new Creator() {

			@Override
			public AbstractDawidSkene create(String id) {
				return new BatchDawidSkene(id);
			}
		});
		
		DS_FACTORY.put("incremental", new Creator() {

			@Override
			public AbstractDawidSkene create(String id) {
				return new IncrementalDawidSkene(id);
			}
		});
	};
	
	public Job createJob(String type, String id){
		type = type.toLowerCase();
		if (!DS_FACTORY.containsKey(type)) {
			return null;
		}
		AbstractDawidSkene ads = DS_FACTORY.get(type).create(id);
		return new Job(ads, id);
	}
}
