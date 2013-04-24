package com.datascience.core.jobs;

import com.datascience.core.algorithms.INewDataObserver;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.IData;
import com.datascience.core.datastoring.memory.InMemoryData;
import com.datascience.core.base.Project;
import com.datascience.core.datastoring.memory.InMemoryResults;
import com.datascience.core.nominal.CategoryValue;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.nominal.NominalAlgorithm;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.results.*;
import com.datascience.core.storages.IJobStorage;
import com.datascience.gal.*;
import com.datascience.galc.ContinuousIpeirotis;
import com.datascience.galc.ContinuousProject;
import com.datascience.mv.BatchMV;
import com.datascience.mv.IncrementalMV;
import com.datascience.scheduler.SchedulerFactory;
import com.datascience.serialization.ISerializer;
import com.datascience.serialization.json.JSONUtils;
import com.datascience.utils.CostMatrix;
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
	protected IJobStorage jobStorage;

	public JobFactory(ISerializer serializer, IJobStorage jobStorage){
		this.serializer = serializer;
		this.jobStorage = jobStorage;
	}

	protected interface AlgorithmCreator {
		NominalAlgorithm create(JsonObject jo);
	}

	protected interface JobCreator{
		Job create(JsonObject jo, String id);
	}

	final static Map<String, AlgorithmCreator> ALG_FACTORY = new HashMap();
	final static Map<String, JobCreator> JOB_FACTORY = new HashMap();
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

		JobCreator nominal = new JobCreator(){
			@Override
			public Job create(JsonObject jo, String id){
				return createNominalJob(jo, id);
			}
		};

		JobCreator continuous = new JobCreator(){
			@Override
			public Job create(JsonObject jo, String id){
				return createContinuousJob(jo, id);
			}
		};
		JOB_FACTORY.put("NOMINAL", nominal);
		JOB_FACTORY.put("CONTINUOUS", continuous);
		JOB_FACTORY.put("GALC", continuous);
		for (String s : ALG_FACTORY.keySet())
			JOB_FACTORY.put(s, nominal);
	}

	protected <T> void handleSchedulerLoading(JsonObject settings, Project project){
		if (settings.has("scheduler"))
			project.setScheduler(new SchedulerFactory<T>().create(settings));
	}

	protected NominalProject getNominalProject(Collection<String> categories,
											   Collection<CategoryValue> categoryPriors,
											   CostMatrix<String> costMatrix,
											   String algorithm,
											   JsonObject jo,
											   String id){
		AlgorithmCreator creator = ALG_FACTORY.get(algorithm);
		if (creator == null){
			throw new IllegalArgumentException(String.format("Unknown Job algorithm: %s", algorithm));
		}
		NominalAlgorithm na = creator.create(jo);
		INominalData data = jobStorage.getNominalData(id);
		IResults<String, DatumResult, WorkerResult> results = jobStorage.getNominalResults(id, categories);
		NominalProject np = new NominalProject(na, data, results);
		if (na instanceof INewDataObserver) {
			na.getData().addNewUpdatableAlgorithm((INewDataObserver) na);
		}
		np.initializeCategories(categories, categoryPriors, costMatrix);
		this.<String>handleSchedulerLoading(jo, np);
		np.setInitializationData(jo);
		return np;
	}
	
	public Job createNominalJob(JsonObject jo, String id){
		if (!jo.has("categories")){
			throw new IllegalArgumentException("You should provide categories list");
		}
		Collection<String> categories = serializer.parse(jo.get("categories").toString(), JSONUtils.stringSetType);
		Collection<CategoryValue> categoryPriors = jo.has("categoryPriors") ?
				(Collection<CategoryValue>) serializer.parse(jo.get("categoryPriors").toString(), JSONUtils.categoryValuesCollectionType) : null;
		CostMatrix<String> costMatrix = jo.has("costMatrix") ?
				(CostMatrix<String>) serializer.parse(jo.get("costMatrix").toString(), CostMatrix.class) : null;
		return new Job(
			getNominalProject(
				categories,
				categoryPriors,
				costMatrix,
				jo.get("algorithm").getAsString(),
				jo,
				id),
			id);
	}
	
	public Job createContinuousJob(JsonObject jo, String id){
		ContinuousIpeirotis alg = new ContinuousIpeirotis();
		alg.setEpsilon(jo.has("epsilon") ? jo.get("epsilon").getAsDouble() : 1e-6);
		alg.setIterations(jo.has("iterations") ? jo.get("iterations").getAsInt() : 10);
		IData<ContValue> data = jobStorage.getContData(id);
		IResults<ContValue, DatumContResults, WorkerContResults> results = jobStorage.getContResults(id);
		ContinuousProject cp = new ContinuousProject(alg, data, results);
		this.<ContValue>handleSchedulerLoading(jo, cp);
		cp.setInitializationData(jo);
		return new Job(cp, id);
	}

	public <T extends Project> Job<T> create(String type, String initializationData, String jsonData,
											 String jsonResults, String model, String id){
		JsonObject jo = new JsonParser().parse(initializationData).getAsJsonObject();
		Job<T> job = JOB_FACTORY.get(type).create(jo, id);
		job.getProject().setData(serializer.<InMemoryData>parse(jsonData, InMemoryData.class));
		job.getProject().setResults(serializer.<InMemoryResults>parse(jsonResults, InMemoryResults.class));
		handleSchedulerLoading(jo, job.getProject());
		job.getProject().getAlgorithm().setModel(serializer.parse(model, job.getProject().getAlgorithm().getModelType()));
		return job;
	}

	public <T extends Project> Job<T> create(String type, JsonObject initializationData, String id){
		return JOB_FACTORY.get(type).create(initializationData, id);
	}
}
