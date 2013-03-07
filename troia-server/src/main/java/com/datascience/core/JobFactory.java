package com.datascience.core;

import com.datascience.gal.AbstractDawidSkene;
import com.datascience.gal.BatchDawidSkene;
import com.datascience.gal.Category;
import com.datascience.gal.IncrementalDawidSkene;
import com.datascience.galc.ContinuousProject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Konrad Kurdej
 */
public class JobFactory {

	protected interface Creator{
		AbstractDawidSkene create(Collection<Category> categories);
	}
	
	final static Map<String, Creator> DS_FACTORY = new HashMap();
	{
		DS_FACTORY.put("batch", new Creator() {

			@Override
			public AbstractDawidSkene create(Collection<Category> categories) {
				return new BatchDawidSkene(categories);
			}
		});
		
		DS_FACTORY.put("incremental", new Creator() {
			@Override
			public AbstractDawidSkene create(Collection<Category> categories) {
				return new IncrementalDawidSkene(categories);
			}
		});
	};
	
	protected Creator getCreator(String type){
		type = type.toLowerCase();
		Creator creator = DS_FACTORY.get(type);
		if (creator == null) {
			throw new IllegalArgumentException("Unknown Job type: " + type);
		}
		return creator;
	}

	public Job createJob(String type, String id, Collection<Category> categories){
		Creator creator = getCreator(type);
		AbstractDawidSkene ads = creator.create(categories);
		return new Job(ads, id);
	}
	
	public Job createContinuousJob(String id){
		return new Job(new ContinuousProject(), id);
	}
}
