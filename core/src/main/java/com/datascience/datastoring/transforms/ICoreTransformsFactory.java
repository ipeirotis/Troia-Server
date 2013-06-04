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

public interface ICoreTransformsFactory<V> {

	ITransformation<JsonObject, V> createSettingsTransform();
	ITransformation<String, V> createKindTransform();

	ITransformation<Collection<AssignedLabel<String>>, V> createNominalAssignsTransformation();
	ITransformation<Collection<LObject<String>>, V> createNominalObjectsTransformation();

	ITransformation<Collection<AssignedLabel<ContValue>>, V> createContAssignsTransformation();
	ITransformation<Collection<LObject<ContValue>>, V> createContObjectsTransformation();

	ITransformation<Collection<Worker>, V> createWorkersTransformation();

	ITransformation<PureNominalData, V> createPureNominalDataTransformation();

	ITransformation<DatumContResults, V> createDatumContResultsTransformation();
	ITransformation<WorkerContResults, V> createWorkerContResultsTransformation();
	ITransformation<DatumResult, V> createDatumStringResultsTransformation();
	ITransformation<WorkerResult, V> createWorkerStringResultsTransformation(ResultsFactory.WorkerResultNominalFactory wrnf);

	String getID();
}
