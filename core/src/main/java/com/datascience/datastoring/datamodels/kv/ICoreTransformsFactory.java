package com.datascience.datastoring.datamodels.kv;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.PureNominalData;
import com.datascience.core.results.*;
import com.datascience.utils.ITransformation;
import com.google.gson.JsonObject;

import java.util.Collection;

public interface ICoreTransformsFactory<V> {

	ITransformation<JsonObject, V> createSettingsTransform();
	ITransformation<String, V> createKindTransform();

	<T>ITransformation<Collection<AssignedLabel<T>>, V> createAssignsTransformation();
	<T>ITransformation<Collection<LObject<T>>, V> createObjectsTransformation();

	<T>ITransformation<Collection<Worker<T>>, String> createWorkersTransformation();

	<T>ITransformation<Collection<Worker<T>>, PureNominalData> createPureNominalDataTransformation();

	ITransformation<DatumContResults, V> createDatumContResultsTransformation();
	ITransformation<WorkerContResults, V> createWorkerContResultsTransformation();
	ITransformation<DatumResult, V> createDatumStringResultsTransformation();
	ITransformation<WorkerResult, V> createWorkerStringResultsTransformation(ResultsFactory.WorkerResultNominalFactory wrnf);

	String getID();
}
