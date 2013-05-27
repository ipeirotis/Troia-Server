package com.datascience.datastoring.datamodels.kv;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.IData;
import com.datascience.core.base.Project;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.nominal.PureNominalData;
import com.datascience.core.results.*;
import com.datascience.datastoring.adapters.kv.IKVsProvider;
import com.datascience.datastoring.adapters.kv.ISafeKVStorage;
import com.datascience.datastoring.jobs.*;
import com.datascience.serialization.ISerializer;
import com.google.gson.JsonObject;

import java.util.Collection;

/**
 * @Author: konrad
 */
public class KVJobStorage extends BaseJobStorage implements IJobDataLoader{

	protected ISafeKVStorage<JsonObject> jobSettings;
	protected ISafeKVStorage<String> jobTypes;
	protected KVCleaner cleaner;
	protected JobFactory jobFactory;
	protected IKVsProvider kvsProvider;

	public KVJobStorage(ISerializer serializer, IKVsProvider kvsProvider){
		super(kvsProvider);
		this.kvsProvider = kvsProvider;
		jobFactory = new JobFactory(serializer, this);
		initializeStorage();
	}

	protected void initializeStorage(){
		cleaner = new KVCleaner();
		jobSettings = kvsProvider.getSettingsKV();
		jobTypes = kvsProvider.getKindsKV();
	}

	@Override
	public <T extends Project> Job<T>  create(String type, String id, JsonObject settings) throws Exception {
		Job<T> job = jobFactory.create(type, settings, id);
		jobTypes.put(job.getId(), job.getProject().getKind());
		jobSettings.put(job.getId(), job.getProject().getInitializationData());
		return job;
	}

	@Override
	public <T extends Project> Job<T> get(String id) throws Exception {
		JsonObject settings = jobSettings.get(id);
		String type = jobTypes.get(id);
		if (type == null || settings == null)
			return null;
		return jobFactory.create(type, settings, id);
	}

	@Override
	public void remove(Job job) throws Exception {
		Project p = job.getProject();
		cleaner.cleanUp((KVResults) p.getResults(), p.getData());
		cleaner.cleanUp((KVData) p.getData()); // order is important ...
		jobTypes.remove("");
		jobSettings.remove("");
	}


	@Override
	public IData<ContValue> getContData(String id) {
		KVData<ContValue> data = new KVData<ContValue>(
				kvsProvider.<Collection<AssignedLabel<ContValue>>>getWorkerAssignsKV(id),
				kvsProvider.getObjectAssignsKV(id),
				kvsProvider.getObjectsKV(id),
				kvsProvider.getGoldObjectsKV(id),
				kvsProvider.getEvaluationObjectsKV(id),
				kvsProvider.getWorkersKV(id)
		);
		return data;
	}

	@Override
	public INominalData getNominalData(String id) {
		INominalData data = new KVNominalData(
				this.<Collection<AssignedLabel<String>>>getKVForJob(id, "WorkerAssigns", true),
				this.<Collection<AssignedLabel<String>>>getKVForJob(id, "ObjectAssigns", true),
				this.<Collection<LObject<String>>>getKVForJob(id, "Objects", false),
				this.<Collection<LObject<String>>>getKVForJob(id, "GoldObjects", false),
				this.<Collection<LObject<String>>>getKVForJob(id, "EvaluationObjects", false),
				this.<Collection<Worker<String>>>getKVForJob(id, "Workers", false),
				this.<PureNominalData>getKVForJob(id, "JobSettings", false)
		);
		return data;
	}

	@Override
	public IResults<ContValue, DatumContResults, WorkerContResults> getContResults(String id) {
		return new KVResults(
				new ResultsFactory.DatumContResultFactory(),
				new ResultsFactory.WorkerContResultFactory(),
				this.<Collection<DatumContResults>>getKVForJob(id, "ObjectResults", true),
				this.<Collection<WorkerContResults>>getKVForJob(id, "WorkerResults", true));
	}

	@Override
	public IResults<String, DatumResult, WorkerResult> getNominalResults(String id, Collection<String> categories) {
		ResultsFactory.WorkerResultNominalFactory wrnf = new ResultsFactory.WorkerResultNominalFactory();
		wrnf.setCategories(categories);
		return new KVResults(
				new ResultsFactory.DatumResultFactory(),
				wrnf,
				this.<Collection<DatumResult>>getKVForJob(id, "ObjectResults", true),
				this.<Collection<WorkerResult>>getKVForJob(id, "WorkerResults", true));
	}

	protected ISafeKVStorage<PureNominalData> getPureNominalDataKV()
}
