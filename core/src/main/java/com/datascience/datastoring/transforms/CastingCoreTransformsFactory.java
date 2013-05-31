package com.datascience.datastoring.transforms;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.PureNominalData;
import com.datascience.core.results.*;
import com.datascience.utils.ITransformation;
import com.google.gson.JsonObject;

import java.util.Collection;

public class CastingCoreTransformsFactory implements ICoreTransformsFactory<Object>{
	@Override
	public ITransformation<JsonObject, Object> createSettingsTransform() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ITransformation<String, Object> createKindTransform() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <T> ITransformation<Collection<AssignedLabel<T>>, Object> createAssignsTransformation() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <T> ITransformation<Collection<LObject<T>>, Object> createObjectsTransformation() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <T> ITransformation<Collection<Worker<T>>, String> createWorkersTransformation() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public <T> ITransformation<Collection<Worker<T>>, PureNominalData> createPureNominalDataTransformation() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ITransformation<DatumContResults, Object> createDatumContResultsTransformation() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ITransformation<WorkerContResults, Object> createWorkerContResultsTransformation() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ITransformation<DatumResult, Object> createDatumStringResultsTransformation() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ITransformation<WorkerResult, Object> createWorkerStringResultsTransformation(ResultsFactory.WorkerResultNominalFactory wrnf) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public String getID() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}
}
