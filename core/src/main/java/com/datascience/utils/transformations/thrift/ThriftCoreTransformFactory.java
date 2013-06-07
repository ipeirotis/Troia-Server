package com.datascience.utils.transformations.thrift;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.results.*;
import com.datascience.datastoring.transforms.ICoreTransformsFactory;
import com.datascience.utils.ITransformation;
import com.datascience.utils.transformations.ComposingTransform;
import com.datascience.utils.transformations.thrift.generated.*;
import com.google.gson.JsonObject;
import org.apache.thrift.TBase;

import java.io.InputStream;
import java.util.Collection;

/**
 * @Author: konrad
 */
public class ThriftCoreTransformFactory implements ICoreTransformsFactory<InputStream> {

	protected <A, B extends TBase> ITransformation<A, InputStream> compose(ITransformation<A, B> base, B object){
		return new ComposingTransform<A, B, InputStream>(base,
				new ThriftCoreTransforms.ThriftBaseTransform<B>(object));
	}

	@Override
	public ITransformation<JsonObject, InputStream> createSettingsTransform() {
		return compose(null, new TSettings());
	}

	@Override
	public ITransformation<String, InputStream> createKindTransform() {
		return compose(null, new TKind());
	}

	@Override
	public ITransformation<Collection<AssignedLabel<String>>, InputStream> createNominalAssignsTransformation() {
		return compose(null, new TAssigns());
	}

	@Override
	public ITransformation<Collection<LObject<String>>, InputStream> createNominalObjectsTransformation() {
		return compose(null, new TLObjects());
	}

	@Override
	public ITransformation<Collection<AssignedLabel<ContValue>>, InputStream> createContAssignsTransformation() {
		return compose(null, new TAssigns());
	}

	@Override
	public ITransformation<Collection<LObject<ContValue>>, InputStream> createContObjectsTransformation() {
		return compose(null, new TLObjects());
	}

	@Override
	public ITransformation<Collection<Worker>, InputStream> createWorkersTransformation() {
		return compose(new ThriftCoreTransforms.WorkersTransform(), new TWorkers());
	}

	@Override
	public ITransformation<DatumContResults, InputStream> createDatumContResultsTransformation() {
		return compose(null, new TDatumContResults());
	}

	@Override
	public ITransformation<WorkerContResults, InputStream> createWorkerContResultsTransformation() {
		return compose(null, new TWorkerContResults());
	}

	@Override
	public ITransformation<DatumResult, InputStream> createDatumStringResultsTransformation() {
		return compose(null, new TDatumNominalResults());
	}

	@Override
	public ITransformation<WorkerResult, InputStream> createWorkerStringResultsTransformation(ResultsFactory.WorkerResultNominalFactory wrnf) {
		return compose(null, new TWorkerNominalResults());
	}

	@Override
	public String getID() {
		return "THRIFT";
	}
}
