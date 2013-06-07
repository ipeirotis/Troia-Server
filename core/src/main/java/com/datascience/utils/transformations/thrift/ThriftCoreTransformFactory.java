package com.datascience.utils.transformations.thrift;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.PureNominalData;
import com.datascience.core.results.*;
import com.datascience.datastoring.transforms.ICoreTransformsFactory;
import com.datascience.utils.ITransformation;
import com.datascience.utils.transformations.ComposingTransform;
import com.datascience.utils.transformations.thrift.generated.TWorkers;
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
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ITransformation<String, InputStream> createKindTransform() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ITransformation<Collection<AssignedLabel<String>>, InputStream> createNominalAssignsTransformation() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ITransformation<Collection<LObject<String>>, InputStream> createNominalObjectsTransformation() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ITransformation<Collection<AssignedLabel<ContValue>>, InputStream> createContAssignsTransformation() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ITransformation<Collection<LObject<ContValue>>, InputStream> createContObjectsTransformation() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ITransformation<Collection<Worker>, InputStream> createWorkersTransformation() {
		return compose(new ThriftCoreTransforms.WorkersTransform(), new TWorkers());
	}

	@Override
	public ITransformation<PureNominalData, InputStream> createPureNominalDataTransformation() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ITransformation<DatumContResults, InputStream> createDatumContResultsTransformation() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ITransformation<WorkerContResults, InputStream> createWorkerContResultsTransformation() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ITransformation<DatumResult, InputStream> createDatumStringResultsTransformation() {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public ITransformation<WorkerResult, InputStream> createWorkerStringResultsTransformation(ResultsFactory.WorkerResultNominalFactory wrnf) {
		return null;  //To change body of implemented methods use File | Settings | File Templates.
	}

	@Override
	public String getID() {
		return "THRIFT";
	}
}
