package com.datascience.datastoring.storages;

import com.datascience.core.base.*;
import com.datascience.datastoring.kv.KVCleaner;
import com.datascience.datastoring.kv.KVData;
import com.datascience.datastoring.kv.KVNominalData;
import com.datascience.datastoring.kv.KVResults;
import com.datascience.datastoring.jobs.IJobStorage;
import com.datascience.datastoring.jobs.Job;
import com.datascience.datastoring.jobs.JobFactory;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.nominal.PureNominalData;
import com.datascience.core.results.*;
import com.datascience.serialization.ISerializer;
import com.datascience.utils.storage.*;
import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.util.Collection;

/**
 * User: artur
 * Date: 5/17/13
 */
public class MemoryKVJobStorage implements IJobStorage{

	protected ISafeKVStorage<JsonObject> jobSettings;
	protected ISafeKVStorage<String> jobTypes;
	protected JobFactory jobFactory;

	public MemoryKVJobStorage(ISerializer serializer){
		jobSettings = getKV("JobSettings");
		jobTypes = getKV("JobTypes");
		jobFactory = new JobFactory(serializer, this);
	}

	protected <V> ISafeKVStorage<V> getKV(String table){
		return new DefaultSafeKVStorage<V>(new MemoryKVStorage<V>(), table);
	}

	protected <V> ISafeKVStorage<V> getKVForJob(String id, String table, boolean multirows){
		return new DefaultSafeKVStorage<V>(new KVKeyPrefixingWrapper<V>(new MemoryKVStorage<V>(), multirows ? id + "_" : id), table);
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
	public void add(Job job) throws Exception {
		// We should check whether this job exists - if not than create new:
		// - row in JobSettings, - row with empty collection in {Objects, Workers}
		jobTypes.put(job.getId(), job.getProject().getKind());
		jobSettings.put(job.getId(), job.getProject().getInitializationData());
	}

	@Override
	public void remove(Job job) throws Exception {
		Project p = job.getProject();
		KVCleaner cleaner = new KVCleaner();
		cleaner.cleanUp((KVResults) p.getResults(), p.getData());
		cleaner.cleanUp((KVData) p.getData()); // order is important ...
		jobTypes.remove("");
		jobSettings.remove("");
	}

	@Override
	public void test() throws Exception {
	}

	@Override
	public void stop() throws Exception {
		jobTypes.shutdown();
		jobSettings.shutdown();

	}

	@Override
	public void clearAndInitialize() throws SQLException {
	}

	@Override
	public IData<ContValue> getContData(String id) {
		KVData<ContValue> data = new KVData<ContValue>(
				this.<Collection<AssignedLabel<ContValue>>>getKVForJob(id, "WorkerAssigns", true),
				this.<Collection<AssignedLabel<ContValue>>>getKVForJob(id, "ObjectAssigns", true),
				this.<Collection<LObject<ContValue>>>getKVForJob(id, "Objects", false),
				this.<Collection<LObject<ContValue>>>getKVForJob(id, "GoldObjects", false),
				this.<Collection<LObject<ContValue>>>getKVForJob(id, "EvaluationObjects", false),
				this.<Collection<Worker<ContValue>>>getKVForJob(id, "Workers", false)
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

	@Override
	public String toString(){
		return "MEMORY_KV";
	}
}
