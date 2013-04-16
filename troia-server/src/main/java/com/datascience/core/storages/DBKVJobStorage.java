package com.datascience.core.storages;

import com.datascience.core.Job;
import com.datascience.core.JobFactory;
import com.datascience.core.base.*;
import com.datascience.core.datastoring.kv.KVCleaner;
import com.datascience.core.datastoring.kv.KVData;
import com.datascience.core.datastoring.kv.KVNominalData;
import com.datascience.core.datastoring.kv.KVResults;
import com.datascience.core.datastoring.memory.InMemoryResults;
import com.datascience.core.datastoring.utils.NominalData;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.results.*;
import com.datascience.serialization.ISerializer;
import com.datascience.serialization.SerializationTransform;
import com.datascience.serialization.json.JSONUtils;
import com.datascience.utils.DBKVHelper;
import com.datascience.utils.ITransformation;
import com.datascience.utils.storage.*;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @Author: konrad
 */
public class DBKVJobStorage implements IJobStorage{

	protected DBKVHelper helper;
	protected ISerializer serializer;
	protected ISafeKVStorage<JsonObject> jobSettings;
	protected ISafeKVStorage<String> jobTypes;
	protected JobFactory jobFactory;

	public DBKVJobStorage(DBKVHelper helper, ISerializer serializer) throws SQLException {
		this.helper = helper;
		this.helper.connectDB();
		this.serializer = serializer;
		jobSettings = getKV("JobSettings", JsonObject.class);
		jobTypes = getKV("JobTypes", String.class);
		jobFactory = new JobFactory(serializer, this);
	}

	protected <V> ISafeKVStorage<V> getJobSettingKV(){   // TODO XXX  set proper type
		IKVStorage<V> kvstorage = null;
//		ITransformation<V, String> transformation = new SerializationTransform<V>(serializer, expectedType);
//		kvstorage = new VTransformingKVWrapper<V, String>(helper.getKV("JobSettings"), transformation);
//		kvstorage = new CachedKV<V>(kvstorage, 15); // this when we will test this
		return new DefaultSafeKVStorage<V>(kvstorage, "JobSettings");
	}

	protected <V> ISafeKVStorage<V> getKV(String table, Type expectedType){
		ITransformation<V, String> transformation = new SerializationTransform<V>(serializer, expectedType);
		IKVStorage<V> storage = new VTransformingKVWrapper<V, String>(helper.getKV(table), transformation);
		return new DefaultSafeKVStorage<V>(storage, table);
	}

	protected <V> ISafeKVStorage<V> getKVForJob(String id, String table, Type expectedType, boolean multirows){
		IKVStorage<V> kvstorage;
		ITransformation<V, String> transformation = new SerializationTransform<V>(serializer, expectedType);
		kvstorage = new VTransformingKVWrapper<V, String>(helper.getKV(table), transformation);
		kvstorage = new KVKeyPrefixingWrapper<V>(kvstorage, multirows ? id + "_" : id);
		return new DefaultSafeKVStorage<V>(kvstorage, table);
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
		// cleaner.cleanUp( TODO XXX )job settings etc;
	}

	@Override
	public void test() throws Exception {
		// TODO XXX I don't have much idea how to do this.. just put something to kv?
	}

	@Override
	public void stop() throws Exception {
		helper.close();
	}

	@Override
	public <T> IData<T> getData(String id) {
		KVData<T> data = new KVData<T>(
				this.<Collection<AssignedLabel<T>>>getKVForJob(id, "WorkerAssigns", JSONUtils.assignsCollection, true),
				this.<Collection<AssignedLabel<T>>>getKVForJob(id, "ObjectAssigns", JSONUtils.assignsCollection, true),
				this.<Collection<LObject<T>>>getKVForJob(id, "Objects", JSONUtils.objectsCollection, false),
				this.<Collection<LObject<T>>>getKVForJob(id, "GoldObjects", JSONUtils.objectsCollection, false),
				this.<Collection<LObject<T>>>getKVForJob(id, "EvaluationObjects", JSONUtils.objectsCollection, false),
				this.<Collection<Worker<T>>>getKVForJob(id, "Workers", JSONUtils.workersCollection, false)
		);
		return data;
	}

	@Override
	public INominalData getNominalData(String id) {
		INominalData data = new KVNominalData(
				this.<Collection<AssignedLabel<String>>>getKVForJob(id, "WorkerAssigns", JSONUtils.assignsCollection, true),
				this.<Collection<AssignedLabel<String>>>getKVForJob(id, "ObjectAssigns", JSONUtils.assignsCollection, true),
				this.<Collection<LObject<String>>>getKVForJob(id, "Objects", JSONUtils.objectsCollection, false),
				this.<Collection<LObject<String>>>getKVForJob(id, "GoldObjects", JSONUtils.objectsCollection, false),
				this.<Collection<LObject<String>>>getKVForJob(id, "EvaluationObjects", JSONUtils.objectsCollection, false),
				this.<Collection<Worker<String>>>getKVForJob(id, "Workers", JSONUtils.workersStringType, false),
				this.<NominalData>getKVForJob(id, "JobSettings", NominalData.class, false)
		);
		return data;
	}

	@Override
	public IResults<ContValue, DatumContResults, WorkerContResults> getContResults(String id) {
		return new KVResults(
				new ResultsFactory.DatumContResultFactory(),
				new ResultsFactory.WorkerContResultFactory(),
				this.<Collection<DatumResult>>getKVForJob(id, "ObjectResults", WorkerResult.class, true),
				this.<Collection<WorkerResult>>getKVForJob(id, "WorkerResults", WorkerResult.class, true));
	}

	@Override
	public IResults<String, DatumResult, WorkerResult> getNominalResults(String id, Collection<String> categories) {
		ResultsFactory.WorkerResultNominalFactory wrnf = new ResultsFactory.WorkerResultNominalFactory();
		wrnf.setCategories(categories);
		return new KVResults(
				new ResultsFactory.DatumResultFactory(),
				wrnf,
				this.<Collection<DatumResult>>getKVForJob(id, "ObjectResults", WorkerResult.class, true),
				this.<Collection<WorkerResult>>getKVForJob(id, "WorkerResults", WorkerResult.class, true));
	}
}
