package com.datascience.core.storages;

import com.datascience.core.Job;
import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Project;
import com.datascience.core.base.Worker;
import com.datascience.core.datastoring.kv.KVCleaner;
import com.datascience.core.datastoring.kv.KVData;
import com.datascience.core.datastoring.kv.KVResults;
import com.datascience.core.results.DatumResult;
import com.datascience.core.results.WorkerResult;
import com.datascience.serialization.ISerializer;
import com.datascience.serialization.SerializationTransform;
import com.datascience.utils.DBKVHelper;
import com.datascience.utils.ITransformation;
import com.datascience.utils.storage.*;

import java.lang.reflect.Type;
import java.util.Collection;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @Author: konrad
 */
public class DBKVJobStorage implements IJobStorage{

	protected DBKVHelper helper;
	protected ISerializer serializer;
	protected ISafeKVStorage<?> jobSettings; // TODO XXX

	public DBKVJobStorage(DBKVHelper helper, ISerializer serializer){
		this.helper = helper;
		this.serializer = serializer;
		jobSettings = getJobSettingKV();
	}

	protected <V> ISafeKVStorage<V> getJobSettingKV(){   // TODO XXX  set proper type
		IKVStorage<V> kvstorage = null;
//		ITransformation<V, String> transformation = new SerializationTransform<V>(serializer, expectedType);
//		kvstorage = new VTransformingKVWrapper<V, String>(helper.getKV("JobSettings"), transformation);
//		kvstorage = new CachedKV<V>(kvstorage, 15); // this when we will test this
		return new DefaultSafeKVStorage<V>(kvstorage, "JobSettings");
	}

	protected <V> ISafeKVStorage<V> getKVForJob(String id, String table, Type expectedType){
		IKVStorage<V> kvstorage;
		ITransformation<V, String> transformation = new SerializationTransform<V>(serializer, expectedType);
		kvstorage = new VTransformingKVWrapper<V, String>(helper.getKV(table), transformation);
		kvstorage = new KVKeyPrefixingWrapper<V>(kvstorage, id + "_");
		return new DefaultSafeKVStorage<V>(kvstorage, table);
	}

	@Override
	public <T extends Project> Job<T> get(String id) throws Exception {
		Object settings = jobSettings.get(id);
		checkArgument(settings != null, "There is no job with ID="+id);
		KVData<T> data = new KVData<T>(   // This needs to create proper instance = KVNominalData
				this.<Collection<AssignedLabel<T>>>getKVForJob(id, "WorkerAssigns", AssignedLabel.class),
				this.<Collection<AssignedLabel<T>>>getKVForJob(id, "ObjectAssigns", AssignedLabel.class),
				this.<Collection<LObject<T>>>getKVForJob(id, "Objects", LObject.class),
				this.<Collection<LObject<T>>>getKVForJob(id, "Objects", LObject.class),
				this.<Collection<LObject<T>>>getKVForJob(id, "Objects", LObject.class), // TODO separate tables for this and golds?
				this.<Collection<Worker<T>>>getKVForJob(id, "Workers", Worker.class)
		);
		KVResults results = new KVResults(null, null, // Needs proper factories
				this.<Collection<DatumResult>>getKVForJob(id, "ObjectResults", WorkerResult.class),
				this.<Collection<WorkerResult>>getKVForJob(id, "WorkerResults", WorkerResult.class));

		//Project project prepare project
		return null;
	}

	@Override
	public void add(Job job) throws Exception {
		// We should check whether this job exists - if not than create new:
		// - row in JobSettings, - row with empty collection in {Objects, Workers}

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
}
