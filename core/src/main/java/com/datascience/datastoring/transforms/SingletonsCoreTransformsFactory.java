package com.datascience.datastoring.transforms;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.PureNominalData;
import com.datascience.core.results.*;
import com.datascience.utils.ITransformation;
import com.google.gson.JsonObject;

import java.util.Collection;

/**
 * Wrapper to make sure that we won't create transforms over and over.
 * @Author: konrad
 */
public class SingletonsCoreTransformsFactory<V> implements ICoreTransformsFactory<V> {

	protected ICoreTransformsFactory<V> inner;

	protected ITransformation<JsonObject, V> settingsTransform;
	protected ITransformation<String, V> kindTransform;

	protected ITransformation<Collection<AssignedLabel<String>>, V> nominalAssignsTransformation;
	protected ITransformation<Collection<LObject<String>>, V> nominalObjectsTransformation;

	protected ITransformation<Collection<AssignedLabel<ContValue>>, V> contAssignsTransformation;
	protected ITransformation<Collection<LObject<ContValue>>, V> contObjectsTransformation;

	protected ITransformation<Collection<Worker>, V> workersTransformation;

	protected ITransformation<PureNominalData, V> pureNominalDataTransformation;

	protected ITransformation<DatumContResults, V> datumContResultsTransformation;
	protected ITransformation<WorkerContResults, V> workerContResultsTransformation;
	protected ITransformation<DatumResult, V> datumStringResultsTransformation;


	public SingletonsCoreTransformsFactory(ICoreTransformsFactory<V> inner){
		this.inner = inner;
		initialize();
	}

	protected void initialize(){
		settingsTransform = inner.createSettingsTransform();
		kindTransform = inner.createKindTransform();
		nominalAssignsTransformation = inner.createNominalAssignsTransformation();
		nominalObjectsTransformation = inner.createNominalObjectsTransformation();
		contAssignsTransformation = inner.createContAssignsTransformation();
		contObjectsTransformation = inner.createContObjectsTransformation();
		workersTransformation = inner.createWorkersTransformation();
		pureNominalDataTransformation = inner.createPureNominalDataTransformation();
		datumContResultsTransformation = inner.createDatumContResultsTransformation();
		workerContResultsTransformation = inner.createWorkerContResultsTransformation();
		datumStringResultsTransformation = inner.createDatumStringResultsTransformation();
	}

	@Override
	public ITransformation<JsonObject, V> createSettingsTransform() {
		return settingsTransform;
	}

	@Override
	public ITransformation<String, V> createKindTransform() {
		return kindTransform;
	}

	@Override
	public ITransformation<Collection<AssignedLabel<String>>, V> createNominalAssignsTransformation() {
		return nominalAssignsTransformation;
	}

	@Override
	public ITransformation<Collection<LObject<String>>, V> createNominalObjectsTransformation() {
		return nominalObjectsTransformation;
	}

	@Override
	public ITransformation<Collection<AssignedLabel<ContValue>>, V> createContAssignsTransformation() {
		return contAssignsTransformation;
	}

	@Override
	public ITransformation<Collection<LObject<ContValue>>, V> createContObjectsTransformation() {
		return contObjectsTransformation;
	}

	@Override
	public ITransformation<Collection<Worker>, V> createWorkersTransformation() {
		return workersTransformation;
	}

	@Override
	public ITransformation<PureNominalData, V> createPureNominalDataTransformation() {
		return pureNominalDataTransformation;
	}

	@Override
	public ITransformation<DatumContResults, V> createDatumContResultsTransformation() {
		return datumContResultsTransformation;
	}

	@Override
	public ITransformation<WorkerContResults, V> createWorkerContResultsTransformation() {
		return workerContResultsTransformation;
	}

	@Override
	public ITransformation<DatumResult, V> createDatumStringResultsTransformation() {
		return datumStringResultsTransformation;
	}

	@Override
	public ITransformation<WorkerResult, V> createWorkerStringResultsTransformation(ResultsFactory.WorkerResultNominalFactory wrnf) {
		return inner.createWorkerStringResultsTransformation(wrnf);
	}

	@Override
	public String getID() {
		return inner.getID();
	}
}
