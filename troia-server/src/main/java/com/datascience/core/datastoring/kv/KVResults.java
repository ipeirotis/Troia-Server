package com.datascience.core.datastoring.kv;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.results.AbstractResults;
import com.datascience.core.results.ResultsFactory;
import com.datascience.utils.storage.ISafeKVStorage;

import java.util.Map;

/**
 * @Author: konrad
 */
public class KVResults<T, U, V> extends AbstractResults<T, U, V> {

	protected ISafeKVStorage<U> datumKV;
	protected ISafeKVStorage<V> workersKV;

	public KVResults(ResultsFactory.DatumResultCreator<T, U> datumCreator, ResultsFactory.WorkerResultCreator<T, V> workerCreator,
					 ISafeKVStorage<U> datumKV, ISafeKVStorage<V> workersKV) {
		super(datumCreator, workerCreator);
		this.datumKV = datumKV;
		this.workersKV = workersKV;
	}

	@Override
	protected U uncheckedGetDatumResults(LObject<T> obj) {
		return datumKV.get(obj.getName());
	}

	@Override
	protected V uncheckedGetWorkerResult(Worker<T> worker) {
		return workersKV.get(worker.getName());
	}

	@Override
	public Map<Worker<T>, V> getWorkerResults() {
		// TODO XXX FIXME
		throw new UnsupportedOperationException("Can't be implemented - remove it from interface!");
	}

	@Override
	public Map<LObject<T>, U> getDatumResults() {
		// TODO XXX FIXME
		throw new UnsupportedOperationException("Can't be implemented - remove it from interface!");
	}

	@Override
	public boolean hasDatumResult(LObject<T> obj) {
		return datumKV.contains(obj.getName());
	}

	@Override
	public void addDatumResult(LObject<T> obj, U result) {
		datumKV.put(obj.getName(), result);
	}

	@Override
	public boolean hasWorkerResult(Worker<T> worker) {
		return workersKV.contains(worker.getName());
	}

	@Override
	public void addWorkerResult(Worker<T> worker, V result) {
		workersKV.put(worker.getName(), result);
	}
}
