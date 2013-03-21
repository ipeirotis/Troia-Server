package com.datascience.core;

import com.datascience.core.algorithms.INewDataObserver;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.Data;
import com.datascience.core.base.Project;
import com.datascience.core.nominal.NominalAlgorithm;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.base.Category;
import com.datascience.core.results.Results;
import com.datascience.gal.*;
import com.datascience.galc.ContinuousIpeirotis;
import com.datascience.galc.ContinuousProject;
import com.datascience.mv.BatchMV;
import com.datascience.mv.IncrementalMV;
import com.datascience.scheduler.IScheduler;
import com.datascience.scheduler.SchedulerFactory;
import com.datascience.serialization.ISerializer;
import com.datascience.serialization.json.JSONUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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

	protected interface AlgorithmCreator {
		NominalAlgorithm create(JsonObject jo);
	}

	protected interface JobCreator{
		Job create(JsonObject jo, String id);
	}

	final static Map<String, JobCreator> JOB_FACTORY = new HashMap();
	{
		JOB_FACTORY.put("NOMINAL", new JobCreator(){
			@Override
			public Job create(JsonObject jo, String id){
				return createNominalJob(jo, id);
			}
		});

		JOB_FACTORY.put("CONTINUOUS", new JobCreator(){
			@Override
			public Job create(JsonObject jo, String id){
				return createContinuousJob(jo, id);
			}
		});
	}

	final static Map<String, AlgorithmCreator> ALG_FACTORY = new HashMap();
	{
		AlgorithmCreator bds = new AlgorithmCreator() {
			@Override
			public NominalAlgorithm create(JsonObject jo) {
				BatchDawidSkene alg = new BatchDawidSkene();
				alg.setEpsilon(jo.has("epsilon") ? jo.get("epsilon").getAsDouble() : 1e-6);
				alg.setIterations(jo.has("iterations") ? jo.get("iterations").getAsInt() : 10);
				return alg;
			}
		};
		AlgorithmCreator ids =  new AlgorithmCreator() {
			@Override
			public NominalAlgorithm create(JsonObject jo) {
				IncrementalDawidSkene alg = new IncrementalDawidSkene();
				alg.setEpsilon(jo.has("epsilon") ? jo.get("epsilon").getAsDouble() : 1e-6);
				alg.setIterations(jo.has("iterations") ? jo.get("iterations").getAsInt() : 10);
				return alg;
			}
		};

		AlgorithmCreator bmv = new AlgorithmCreator() {
			@Override
			public NominalAlgorithm create(JsonObject jo) {
				return new BatchMV();
			}
		};

		AlgorithmCreator imv = new AlgorithmCreator() {
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
		AlgorithmCreator creator = ALG_FACTORY.get(algorithm);
		if (creator == null){
			throw new IllegalArgumentException(String.format("Unknown Job algorithm: %s", algorithm));
		}
		NominalAlgorithm na = creator.create(jo);
		NominalProject np = new NominalProject(na);
		if (na instanceof INewDataObserver) {
			na.getData().addNewUpdatableAlgorithm((INewDataObserver) na);
		}
		np.initializeCategories(categories);
		if (jo.has("scheduler"))
			np.setScheduler(new SchedulerFactory<String>().create(jo));
		np.setInitializationData(jo);
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
		ContinuousProject cp = new ContinuousProject(alg);
		if (jo.has("scheduler"))
			cp.setScheduler(new SchedulerFactory<ContValue>().create(jo));
		cp.setInitializationData(jo);
		return new Job(cp, id);
	}

	public <T extends Project> Job<T> create(String type, String initializationData, String jsonData,
											 String jsonResults, String id){
		JsonObject jo = new JsonParser().parse(initializationData).getAsJsonObject();
		Job<T> job = JOB_FACTORY.get(type).create(jo, id);
		job.getProject().setData(serializer.<Data>parse(jsonData, Data.class));
		job.getProject().setResults(serializer.<Results>parse(jsonResults, Results.class));
		job.getProject().setScheduler(new SchedulerFactory<T>().create(jo));
		return job;
	}
}
