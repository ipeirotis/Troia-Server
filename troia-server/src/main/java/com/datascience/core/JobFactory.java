package com.datascience.core;

import com.datascience.core.nominal.NominalProject;
import com.datascience.core.base.Category;
import com.datascience.gal.*;
import com.datascience.galc.ContinuousIpeirotis;
import com.datascience.galc.ContinuousProject;
import com.datascience.serialization.ISerializer;
import com.datascience.serialization.json.JSONUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * @author Konrad Kurdej
 */
public class JobFactory {

	protected ISerializer serializer;

	public JobFactory(ISerializer serializer){
		this.serializer = serializer;
	}

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


	public Job createNominalJob(JsonObject jo, String id){
		Creator creator = getCreator(jo.has("type") ? jo.get("type").getAsString() : "batch");
		if (!jo.has("categories")){
			throw new IllegalArgumentException("You should provide categories list");
		}
		Collection<Category> categories = serializer.parse(jo.get("categories").toString(), JSONUtils.categorySetType);
		return new Job(creator.create(categories), id);
	}
	
	public Job createContinuousJob(JsonObject jo, String id){
		return new Job(new ContinuousProject(new ContinuousIpeirotis()), id);
	}
}
