package com.datascience.datastoring.datamodels.kv;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.PureNominalData;
import com.datascience.core.results.*;
import com.datascience.datastoring.IBackend;
import com.datascience.datastoring.adapters.kv.*;
import com.datascience.datastoring.transforms.ICoreTransformsFactory;
import com.datascience.utils.ITransformation;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import java.util.Collection;
import java.util.List;

public class TransformingKVsProvider<T> extends BaseKVsProvider {

	static protected List<String> KVs = Lists.newArrayList(
			"JobSettings", "JobTypes", "WorkerAssigns", "ObjectAssigns",
			"GoldObjects","EvaluationObjects", "ObjectResults", "WorkerResult"
			);

	protected IBackendKVFactory<T> kvFactory;
	protected ICoreTransformsFactory<T> transformsFactory;

	public TransformingKVsProvider(IBackendKVFactory<T> kvFactory, ICoreTransformsFactory<T> transformsFactory){
		this.kvFactory = kvFactory;
		this.transformsFactory = transformsFactory;
	}

	protected <V> ISafeKVStorage<V> finishKV(IKVStorage<T> storage, ITransformation<V, T> transformation){
		return makeSafeKV(new VTransformingKVWrapper<V, T>(storage, transformation));
	}

	protected <V> ISafeKVStorage<V> getGeneralKV(String table, ITransformation<V, T> transformation){
		return finishKV(kvFactory.getKV(table), transformation);
	}

	protected <V> ISafeKVStorage<V> getKVForJob(String jobid, String table, ITransformation transformation){
		IKVStorage<T> kvstorage = kvFactory.getKV(table);
		kvstorage = new KVKeyPrefixingWrapper<T>(kvstorage, jobid + "_");
		return finishKV(kvstorage, transformation);
	}

	@Override
	protected ISafeKVStorage<JsonObject> _getSettingsKV() {
		return getGeneralKV("JobSettings", transformsFactory.createSettingsTransform());
	}

	@Override
	protected ISafeKVStorage<String> _getKindsKV() {
		return getGeneralKV("JobTypes", transformsFactory.createKindTransform());
	}

	@Override
	public ISafeKVStorage<Collection<AssignedLabel<String>>> getNominalWorkerAssignsKV(String id) {
		return getKVForJob(id, "WorkerAssigns", transformsFactory.createNominalAssignsTransformation());
	}

	@Override
	public ISafeKVStorage<Collection<AssignedLabel<String>>> getNominalObjectAssignsKV(String id) {
		return getKVForJob(id, "ObjectAssigns", transformsFactory.createNominalAssignsTransformation());
	}

	@Override
	public ISafeKVStorage<Collection<LObject<String>>> getNominalObjectsKV(String id) {
		return getKVForJob(id, "Objects", transformsFactory.createNominalObjectsTransformation());
	}

	@Override
	public ISafeKVStorage<Collection<LObject<String>>> getNominalGoldObjectsKV(String id) {
		return getKVForJob(id, "GoldObjects", transformsFactory.createNominalObjectsTransformation());
	}

	@Override
	public ISafeKVStorage<Collection<LObject<String>>> getNominalEvaluationObjectsKV(String id) {
		return getKVForJob(id, "EvaluationObjects", transformsFactory.createNominalObjectsTransformation());
	}

	@Override
	public ISafeKVStorage<Collection<AssignedLabel<ContValue>>> getContWorkerAssignsKV(String id) {
		return getKVForJob(id, "WorkerAssigns", transformsFactory.createContAssignsTransformation());
	}

	@Override
	public ISafeKVStorage<Collection<AssignedLabel<ContValue>>> getContObjectAssignsKV(String id) {
		return getKVForJob(id, "ObjectAssigns", transformsFactory.createContAssignsTransformation());
	}

	@Override
	public ISafeKVStorage<Collection<LObject<ContValue>>> getContObjectsKV(String id) {
		return getKVForJob(id, "Objects", transformsFactory.createContObjectsTransformation());
	}

	@Override
	public ISafeKVStorage<Collection<LObject<ContValue>>> getContGoldObjectsKV(String id) {
		return getKVForJob(id, "GoldObjects", transformsFactory.createContObjectsTransformation());
	}

	@Override
	public ISafeKVStorage<Collection<LObject<ContValue>>> getContEvaluationObjectsKV(String id) {
		return getKVForJob(id, "EvaluationObjects", transformsFactory.createContObjectsTransformation());
	}


	@Override
	public ISafeKVStorage<Collection<Worker>> getWorkersKV(String id) {
		return getKVForJob(id, "Workers", transformsFactory.createWorkersTransformation());
	}

	@Override
	public ISafeKVStorage<PureNominalData> getNominalJobSettingsKV(String id) {
		return getKVForJob(id, "JobSettings", transformsFactory.createPureNominalDataTransformation());
	}

	@Override
	public ISafeKVStorage<Collection<DatumContResults>> getDatumContResultsKV(String id) {
		return getKVForJob(id, "ObjectResults", transformsFactory.createDatumContResultsTransformation());
	}

	@Override
	public ISafeKVStorage<Collection<WorkerContResults>> getWorkerContResultsKV(String id) {
		return getKVForJob(id, "WorkerResults", transformsFactory.createWorkerContResultsTransformation());
	}

	@Override
	public ISafeKVStorage<Collection<DatumResult>> getDatumResultsKV(String id, ResultsFactory.DatumResultFactory resultFactory) {
		return getKVForJob(id, "ObjectResults", transformsFactory.createDatumStringResultsTransformation());
	}

	@Override
	public ISafeKVStorage<Collection<WorkerResult>> getWorkerResultsKV(String id, ResultsFactory.WorkerResultNominalFactory resultFactory) {
		return getKVForJob(id, "WorkerResults", transformsFactory.createWorkerStringResultsTransformation(resultFactory));
	}

	@Override
	public IBackend getBackend() {
		return kvFactory.getBackend();
	}

	@Override
	public void clear() throws Exception {
		for (String kv: KVs){
			kvFactory.remove(kv);
		}
	}

	@Override
	public void rebuild() throws Exception {
		for (String kv: KVs){
			kvFactory.getKV(kv);
		}
	}

	@Override
	public String getID() {
		return kvFactory.getID() + "_" + transformsFactory.getID();
	}

	@Override
	public String toString(){
		return getID();
	}
}
