package com.datascience.datastoring.transforms;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.PureNominalData;
import com.datascience.core.results.*;
import com.datascience.serialization.ISerializer;
import com.datascience.serialization.SerializationTransform;
import com.datascience.utils.ITransformation;
import com.google.common.reflect.TypeToken;

import java.util.Collection;

/**
 * @Author: konrad
 */
public class SerializerBasedCoreTransformsFactory extends BaseStringCoreTransformsFactory{

	protected ISerializer serializer;

	public SerializerBasedCoreTransformsFactory(ISerializer serializer){
		this.serializer = serializer;
	}

	@Override
	public <T> ITransformation<Collection<AssignedLabel<T>>, String> createAssignsTransformation() {
		return new SerializationTransform<Collection<AssignedLabel<T>>>(serializer,
				new TypeToken<Collection<AssignedLabel<T>>>(){}.getType()); // TODO XXX FIXME
	}

	@Override
	public <T> ITransformation<Collection<LObject<T>>, String> createObjectsTransformation() {
		return new SerializationTransform<Collection<LObject<T>>>(serializer,
				new TypeToken<Collection<LObject<T>>>(){}.getType()); // TODO XXX FIXME
	}

	@Override
	public ITransformation<Collection<Worker>, String> createWorkersTransformation() {
		return new SerializationTransform<Collection<Worker>>(serializer,
				new TypeToken<Collection<Worker>>(){}.getType()); // TODO XXX FIXME
	}

	@Override
	public ITransformation<PureNominalData, String> createPureNominalDataTransformation() {
		return new SerializationTransform<PureNominalData>(serializer, PureNominalData.class); // TODO XXX FIXME
	}

	@Override
	public ITransformation<DatumContResults, String> createDatumContResultsTransformation() {
		return new SerializationTransform<DatumContResults>(serializer, DatumContResults.class);
	}

	@Override
	public ITransformation<WorkerContResults, String> createWorkerContResultsTransformation() {
		return new SerializationTransform<WorkerContResults>(serializer, WorkerContResults.class);
	}

	@Override
	public ITransformation<DatumResult, String> createDatumStringResultsTransformation() {
		return new SerializationTransform<DatumResult>(serializer, DatumResult.class);
	}

	@Override
	public ITransformation<WorkerResult, String> createWorkerStringResultsTransformation(ResultsFactory.WorkerResultNominalFactory wrnf) {
		return new SerializationTransform<WorkerResult>(serializer, WorkerResult.class);
	}

	@Override
	public String getID() {
		return serializer.getMediaType(); // TODO XXX FIXME this can be very wrong .. we should add getID to serializer
	}
}
