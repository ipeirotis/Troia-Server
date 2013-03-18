package com.datascience.core;

import com.datascience.core.nominal.NominalAlgorithm;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.base.Category;
import com.datascience.gal.*;
import com.datascience.galc.ContinuousIpeirotis;
import com.datascience.galc.ContinuousProject;
import com.datascience.mv.BatchMV;
import com.datascience.mv.IncrementalMV;
import com.datascience.serialization.ISerializer;
import com.datascience.serialization.json.JSONUtils;
import com.google.gson.JsonObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Konrad Kurdej
 */
public class JobFactory {

	protected ISerializer serializer;

	public JobFactory(ISerializer serializer){
		this.serializer = serializer;
	}

	protected interface Creator{
		NominalAlgorithm create(JsonObject jo);
	}

	final static Map<String, Creator> ALG_FACTORY = new HashMap();
	{
		Creator bds = new Creator() {
			@Override
			public NominalAlgorithm create(JsonObject jo) {
				BatchDawidSkene alg = new BatchDawidSkene();
				alg.setEpsilon(jo.has("epsilon") ? jo.get("epsilon").getAsDouble() : 1e-6);
				alg.setIterations(jo.has("iterations") ? jo.get("iterations").getAsInt() : 10);
				return alg;
			}
		};
		Creator ids =  new Creator() {
			@Override
			public NominalAlgorithm create(JsonObject jo) {
				IncrementalDawidSkene alg = new IncrementalDawidSkene();
				alg.setEpsilon(jo.has("epsilon") ? jo.get("epsilon").getAsDouble() : 1e-6);
				alg.setIterations(jo.has("iterations") ? jo.get("iterations").getAsInt() : 10);
				return alg;
			}
		};

		Creator bmv = new Creator() {
			@Override
			public NominalAlgorithm create(JsonObject jo) {
				return new BatchMV();
			}
		};

		Creator imv = new Creator() {
			@Override
			public NominalAlgorithm create(JsonObject jo) {
				return new IncrementalMV();
			}
		};

		ALG_FACTORY.put("BDS", bds);
		ALG_FACTORY.put("blockingEM", bds);
		ALG_FACTORY.put("IDS", ids);
		ALG_FACTORY.put("onlineEM", ids);
		ALG_FACTORY.put("BMV", bmv);
		ALG_FACTORY.put("blockingMV", bmv);
		ALG_FACTORY.put("IMV", imv);
		ALG_FACTORY.put("onlineMV", imv);
	};

	protected NominalProject getNominalProject(Collection<Category> categories, String algorithm, JsonObject jo){
		Creator creator = ALG_FACTORY.get(algorithm);
		if (creator == null){
			throw new IllegalArgumentException(String.format("Unknown Job algorithm: %s", algorithm));
		}
		NominalProject np = new NominalProject(creator.create(jo));
		np.initializeCategories(categories);
		return np;
	}
	
	public Job createNominalJob(JsonObject jo, String id){
		if (!jo.has("categories")){
			throw new IllegalArgumentException("You should provide categories list");
		}
		Collection<Category> categories = serializer.parse(jo.get("categories").toString(), JSONUtils.categorySetType);

		return new Job(
			getNominalProject(
				categories,
				jo.has("algorithm") ? jo.get("algorithm").getAsString() : "BDS",
				jo),
			id);
	}
	
	public Job createContinuousJob(JsonObject jo, String id){
		ContinuousIpeirotis alg = new ContinuousIpeirotis();
		alg.setEpsilon(jo.has("epsilon") ? jo.get("epsilon").getAsDouble() : 1e-6);
		alg.setIterations(jo.has("iterations") ? jo.get("iterations").getAsInt() : 10);
		return new Job(new ContinuousProject(alg), id);
	}
}
