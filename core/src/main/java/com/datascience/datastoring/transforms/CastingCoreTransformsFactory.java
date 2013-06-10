package com.datascience.datastoring.transforms;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.datastoring.datamodels.memory.IncrementalNominalModel;
import com.datascience.datastoring.datamodels.memory.NominalModel;
import com.datascience.core.results.*;
import com.datascience.utils.ITransformation;
import com.datascience.utils.transformations.CastingTransform;
import com.google.gson.JsonObject;

import java.lang.reflect.Type;
import java.util.Collection;

public class CastingCoreTransformsFactory implements ICoreTransformsFactory<Object> {

	@Override
	public ITransformation<JsonObject, Object> createSettingsTransform() {
		return new CastingTransform<JsonObject>();
	}

	@Override
	public ITransformation<String, Object> createKindTransform() {
		return new CastingTransform<String>();
	}

	@Override
	public ITransformation<Collection<AssignedLabel<String>>, Object> createNominalAssignsTransformation() {
		return new CastingTransform<Collection<AssignedLabel<String>>>();
	}

	@Override
	public ITransformation<Collection<LObject<String>>, Object> createNominalObjectsTransformation() {
		return new CastingTransform<Collection<LObject<String>>>();
	}

	@Override
	public ITransformation<NominalModel, Object> createNominalModelTransformation() {
		return new CastingTransform<NominalModel>();
	}

	@Override
	public ITransformation<IncrementalNominalModel, Object> createIncrementalNominalModelTransformation() {
		return new CastingTransform<IncrementalNominalModel>();
	}

	@Override
	public ITransformation<Collection<AssignedLabel<ContValue>>, Object> createContAssignsTransformation() {
		return new CastingTransform<Collection<AssignedLabel<ContValue>>>();
	}

	@Override
	public ITransformation<Collection<LObject<ContValue>>, Object> createContObjectsTransformation() {
		return new CastingTransform<Collection<LObject<ContValue>>>();
	}

	@Override
	public ITransformation<Collection<Worker>, Object> createWorkersTransformation() {
		return new CastingTransform<Collection<Worker>>();
	}

	@Override
	public ITransformation<DatumContResults, Object> createDatumContResultsTransformation() {
		return new CastingTransform<DatumContResults>();
	}

	@Override
	public ITransformation<WorkerContResults, Object> createWorkerContResultsTransformation() {
		return new CastingTransform<WorkerContResults>();
	}

	@Override
	public ITransformation<DatumResult, Object> createDatumStringResultsTransformation() {
		return new CastingTransform<DatumResult>();
	}

	@Override
	public ITransformation<WorkerResult, Object> createWorkerStringResultsTransformation(ResultsFactory.WorkerResultNominalFactory wrnf) {
		return new CastingTransform<WorkerResult>();
	}

	@Override
	public String getID() {
		return "MEMORY";
	}
}
