package com.datascience.datastoring.storages;

import com.datascience.core.nominal.PureNominalData;
import com.datascience.core.jobs.Job;
import com.datascience.core.base.*;
import com.datascience.datastoring.kv.KVCleaner;
import com.datascience.datastoring.kv.KVData;
import com.datascience.datastoring.kv.KVNominalData;
import com.datascience.datastoring.kv.KVResults;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.results.*;
import com.datascience.serialization.ISerializer;
import com.datascience.serialization.SerializationTransform;
import com.datascience.utils.DBKVHelper;
import com.datascience.utils.ITransformation;
import com.datascience.utils.storage.*;
import com.datascience.utils.transformations.*;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.Collection;


/**
 * @Author: konrad
 */
public class DBKVJobStorage extends BaseDBJobStorage<DBKVHelper>{

	protected ISafeKVStorage<JsonObject> jobSettings;
	protected ISafeKVStorage<String> jobTypes;
	protected TransformationsFactory.ITransformationCreator transforationCreator;

	public DBKVJobStorage(DBKVHelper helper, ISerializer serializer, String transformation) throws SQLException {
		super(helper, serializer);
		jobSettings = getKV("JobSettings", JsonObject.class);
		jobTypes = getKV("JobTypes", String.class);
		transforationCreator = TransformationsFactory.create(transformation);
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

	protected <V> ISafeKVStorage<V> getKVForJob(String id, String table, ITransformation transformation, boolean multirows){
		IKVStorage<V> kvstorage = new VTransformingKVWrapper<V, String>(helper.getKV(table), transformation);
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
		jobTypes.remove("");
		jobSettings.remove("");
	}

	@Override
	public void test() throws Exception {
		//check if db exists
		helper.useDatabase();
		helper.checkTables();
	}

	@Override
	public IData<ContValue> getContData(String id) {
		ITransformation assignCollectionTransform = transforationCreator.createContAssignsTransformation();
		ITransformation objectCollectionTransform = transforationCreator.createContObjectsTransformation();
		ITransformation workerCollectionTransform = transforationCreator.createWorkersTransformation();

		KVData<ContValue> data = new KVData<ContValue>(
				this.<Collection<AssignedLabel<ContValue>>>getKVForJob(id, "WorkerAssigns", assignCollectionTransform, true),
				this.<Collection<AssignedLabel<ContValue>>>getKVForJob(id, "ObjectAssigns", assignCollectionTransform, true),
				this.<Collection<LObject<ContValue>>>getKVForJob(id, "Objects", objectCollectionTransform, false),
				this.<Collection<LObject<ContValue>>>getKVForJob(id, "GoldObjects", objectCollectionTransform, false),
				this.<Collection<LObject<ContValue>>>getKVForJob(id, "EvaluationObjects", objectCollectionTransform, false),
				this.<Collection<Worker<ContValue>>>getKVForJob(id, "Workers", workerCollectionTransform, false)
		);
		return data;
	}

	@Override
	public INominalData getNominalData(String id) {
		ITransformation assignCollectionTransform = transforationCreator.createStringAssignsTransformation();
		ITransformation objectCollectionTransform = transforationCreator.createStringObjectsTransformation();
		ITransformation workerCollectionTransform = transforationCreator.createWorkersTransformation();

		INominalData data = new KVNominalData(
				this.<Collection<AssignedLabel<String>>>getKVForJob(id, "WorkerAssigns", assignCollectionTransform, true),
				this.<Collection<AssignedLabel<String>>>getKVForJob(id, "ObjectAssigns", assignCollectionTransform, true),
				this.<Collection<LObject<String>>>getKVForJob(id, "Objects", objectCollectionTransform, false),
				this.<Collection<LObject<String>>>getKVForJob(id, "GoldObjects", objectCollectionTransform, false),
				this.<Collection<LObject<String>>>getKVForJob(id, "EvaluationObjects", objectCollectionTransform, false),
				this.<Collection<Worker<String>>>getKVForJob(id, "Workers", workerCollectionTransform, false),
				this.<PureNominalData>getKVForJob(id, "JobSettings", PureNominalData.class, false)
		);
		return data;
	}

	@Override
	public IResults<ContValue, DatumContResults, WorkerContResults> getContResults(String id) {
		return new KVResults(
				new ResultsFactory.DatumContResultFactory(),
				new ResultsFactory.WorkerContResultFactory(),
				this.<Collection<DatumContResults>>getKVForJob(id, "ObjectResults",
						transforationCreator.createDatumContResultsTransformation(), true),
				this.<Collection<WorkerContResults>>getKVForJob(id, "WorkerResults",
						transforationCreator.createWorkerContResultsTransformation(), true));
	}

	@Override
	public IResults<String, DatumResult, WorkerResult> getNominalResults(String id, Collection<String> categories) {
		ResultsFactory.WorkerResultNominalFactory wrnf = new ResultsFactory.WorkerResultNominalFactory();
		wrnf.setCategories(categories);
		return new KVResults(
				new ResultsFactory.DatumResultFactory(),
				wrnf,
				this.<Collection<DatumResult>>getKVForJob(id, "ObjectResults",
						transforationCreator.createDatumStringResultsTransformation(), true),
				this.<Collection<WorkerResult>>getKVForJob(id, "WorkerResults",
						transforationCreator.createWorkerStringResultsTransformation(wrnf), true));
	}

	@Override
	public String toString(){
		return "DB_KV";
	}
}
