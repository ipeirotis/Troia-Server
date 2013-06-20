package com.datascience.datastoring.transforms;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.datastoring.datamodels.memory.IncrementalNominalModel;
import com.datascience.datastoring.datamodels.memory.NominalModel;
import com.datascience.core.results.*;
import com.datascience.utils.ITransformation;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.Collection;

public interface ICoreTransformsFactory<V> {

	ITransformation<JsonObject, V> createSettingsTransform();
	ITransformation<String, V> createKindTransform();

	ITransformation<Collection<AssignedLabel<String>>, V> createNominalAssignsTransformation();
	ITransformation<Collection<LObject<String>>, V> createNominalObjectsTransformation();
	ITransformation<NominalModel, V> createNominalModelTransformation();
	ITransformation<IncrementalNominalModel, V> createIncrementalNominalModelTransformation();

	ITransformation<Collection<AssignedLabel<ContValue>>, V> createContAssignsTransformation();
	ITransformation<Collection<LObject<ContValue>>, V> createContObjectsTransformation();

	ITransformation<Collection<Worker>, V> createWorkersTransformation();

	ITransformation<DatumContResults, V> createDatumContResultsTransformation();
	ITransformation<WorkerContResults, V> createWorkerContResultsTransformation();
	ITransformation<DatumResult, V> createDatumStringResultsTransformation();
	ITransformation<WorkerResult, V> createWorkerStringResultsTransformation(ResultsFactory.WorkerResultNominalFactory wrnf);

	String getID();
}
