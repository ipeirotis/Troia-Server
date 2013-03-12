package com.datascience.core;

import com.datascience.core.base.NominalProject;
import com.datascience.core.stats.Category;
import com.datascience.gal.*;
import com.datascience.galc.ContinuousIpeirotis;
import com.datascience.galc.ContinuousProject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Konrad Kurdej
 */
public class JobFactory {

	protected interface Creator{
		NominalProject create(Collection<Category> categories);
	}
	
	final static Map<String, Creator> DS_FACTORY = new HashMap();
	{
		DS_FACTORY.put("batch", new Creator() {

			@Override
			public NominalProject create(Collection<Category> categories) {
				NominalProject np = new NominalProject(new BatchDawidSkene());
				np.initializeCategories(categories);
				return np;
			}
		});
		
		DS_FACTORY.put("incremental", new Creator() {
			@Override
			public NominalProject create(Collection<Category> categories) {
				NominalProject np = new NominalProject(new IncrementalDawidSkene());
				np.initializeCategories(categories);
				return np;
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

	public Job createNominalJob(String type, String id, Collection<Category> categories){
		Creator creator = getCreator(type);
		return new Job(creator.create(categories), id);
	}
	
	public Job createContinuousJob(String id){
		return new Job(new ContinuousProject(new ContinuousIpeirotis()), id);
	}
}
