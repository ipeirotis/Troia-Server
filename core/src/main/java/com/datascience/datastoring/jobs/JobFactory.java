package com.datascience.datastoring.jobs;

import com.datascience.core.algorithms.INewDataObserver;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.IData;
import com.datascience.datastoring.datamodels.memory.InMemoryData;
import com.datascience.core.base.Project;
import com.datascience.datastoring.datamodels.memory.InMemoryNominalData;
import com.datascience.datastoring.datamodels.memory.InMemoryResults;
import com.datascience.core.nominal.CategoryValue;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.nominal.NominalAlgorithm;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.results.*;
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
import com.google.gson.reflect.TypeToken;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.datascience.serialization.json.JSONUtils.t;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * TODO XXX FIXME make separate factory for algorithms and use it here
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
//		Job create(String initializationData, String jsonData, String jsonResults, String model, String id);
	}

	final Map<String, AlgorithmCreator> ALG_FACTORY = new HashMap();
	final Map<String, JobCreator> JOB_FACTORY = new HashMap();
	{

		AlgorithmCreator bds = new AlgorithmCreator() {
			@Override
			public NominalAlgorithm create(JsonObject jo) {
				BatchDawidSkene alg = new BatchDawidSkene();
				alg.setEpsilon(jo.has(Constants.EPSILON) ? jo.get(Constants.EPSILON).getAsDouble() : 1e-6);
				alg.setIterations(jo.has(Constants.ITERATIONS) ? jo.get(Constants.ITERATIONS).getAsInt() : 10);
				return alg;
			}
		};
		AlgorithmCreator ids =  new AlgorithmCreator() {
			@Override
			public NominalAlgorithm create(JsonObject jo) {
				IncrementalDawidSkene alg = new IncrementalDawidSkene();
				alg.setEpsilon(jo.has(Constants.EPSILON) ? jo.get(Constants.EPSILON).getAsDouble() : 1e-6);
				alg.setIterations(jo.has(Constants.ITERATIONS) ? jo.get(Constants.ITERATIONS).getAsInt() : 10);
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

		ALG_FACTORY.put(Constants.BDS, bds);
		ALG_FACTORY.put(Constants.BLOCKING_EM, bds);
		ALG_FACTORY.put(Constants.IDS, ids);
		ALG_FACTORY.put(Constants.ONLINE_EM, ids);
		ALG_FACTORY.put(Constants.BMV, bmv);
		ALG_FACTORY.put(Constants.BLOCKING_MV, bmv);
		ALG_FACTORY.put(Constants.IMV, imv);
		ALG_FACTORY.put(Constants.ONLINE_MV, imv);

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
		JOB_FACTORY.put(Constants.NOMINAL, nominal);
		JOB_FACTORY.put(Constants.CONTINUOUS, continuous);
		JOB_FACTORY.put(Constants.GALC, continuous);
		for (String s : ALG_FACTORY.keySet())
			JOB_FACTORY.put(s, nominal);
	}

	protected <T> void handleSchedulerLoading(JsonObject settings, Project project){
		if (settings.has(Constants.SCHEDULER))
			project.setScheduler(new SchedulerFactory<T>().create(settings));
	}

	protected NominalProject getNominalProject(Collection<String> categories,
											   Collection<CategoryValue> categoryPriors,
											   CostMatrix<String> costMatrix,
											   String algorithm,
											   JsonObject jo,
											   String id){
		checkArgument(ALG_FACTORY.containsKey(t(algorithm)), "Unknown Job algorithm: ", algorithm);
		NominalAlgorithm na = ALG_FACTORY.get(t(algorithm)).create(jo);
		INominalData data = jobStorage.getNominalData(id);
		IResults<String, DatumResult, WorkerResult> results = jobStorage.getNominalResults(id, categories);
		NominalProject np = new NominalProject(na, data, results);
		if (na instanceof INewDataObserver) {
			na.getData().addNewUpdatableAlgorithm((INewDataObserver) na);
		}
		na.setModel(jobStorage.getNominalModel(id, na.getModelType()));
		np.initializeCategories(categories, categoryPriors, costMatrix);
		this.<String>handleSchedulerLoading(jo, np);
		np.setInitializationData(jo);
		return np;
	}
	
	public Job createNominalJob(JsonObject jo, String id){
		checkArgument(jo.has(Constants.CATEGORIES), "You should provide categories list");
		Collection<String> categories = serializer.parse(jo.get(Constants.CATEGORIES).toString(), JSONUtils.stringSetType);
		Collection<CategoryValue> categoryPriors = jo.has(Constants.CATEGORY_PRIORS) ?
				(Collection<CategoryValue>) serializer.parse(jo.get(Constants.CATEGORY_PRIORS).toString(), JSONUtils.categoryValuesCollectionType) : null;
		CostMatrix<String> costMatrix = jo.has(Constants.COST_MATRIX) ?
				(CostMatrix<String>) serializer.parse(jo.get(Constants.COST_MATRIX).toString(), CostMatrix.class) : null;
		return new Job(
			getNominalProject(
				categories,
				categoryPriors,
				costMatrix,
				jo.get(Constants.ALGORITM).getAsString(),
				jo,
				id),
			id);
	}
	
	public Job createContinuousJob(JsonObject jo, String id){
		ContinuousIpeirotis alg = new ContinuousIpeirotis();
		alg.setEpsilon(jo.has(Constants.EPSILON) ? jo.get(Constants.EPSILON).getAsDouble() : 1e-6);
		alg.setIterations(jo.has(Constants.ITERATIONS) ? jo.get(Constants.ITERATIONS).getAsInt() : 10);
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
		Job<T> job = create(type, jo, id);
		//TODO: add new metod to JobCreator
		if (type.equals(NominalProject.kind)) {
			InMemoryNominalData data = serializer.parse(jsonData, InMemoryNominalData.class);
			job.getProject().setResults(serializer.<InMemoryResults<String, DatumResult, WorkerResult>>parse(jsonResults, new TypeToken<InMemoryResults<String, DatumResult, WorkerResult>>() {}.getType()));
			job.getProject().setData(data);
			((ResultsFactory.WorkerResultNominalFactory)((AbstractResults<String, DatumResult, WorkerResult>)job.getProject().getResults()).getWorkerResultsCreator()).setCategories(data.getCategories());
		}
		else {
			job.getProject().setResults(serializer.<InMemoryResults<ContValue, DatumContResults, WorkerContResults>>parse(jsonResults, new TypeToken<InMemoryResults<ContValue, DatumContResults, WorkerContResults>>() {}.getType()));
			job.getProject().setData(serializer.<InMemoryData>parse(jsonData, InMemoryData.class));
		}
		job.getProject().getAlgorithm().setModel(serializer.parse(model, job.getProject().getAlgorithm().getModelType()));
		handleSchedulerLoading(jo, job.getProject());
		return job;
	}

	public <T extends Project> Job<T> create(String type, JsonObject initializationData, String id){
		checkArgument(JOB_FACTORY.containsKey(t(type)), "Unknown algorithm type: ", type);
		return JOB_FACTORY.get(t(type)).create(initializationData, id);
	}
}
