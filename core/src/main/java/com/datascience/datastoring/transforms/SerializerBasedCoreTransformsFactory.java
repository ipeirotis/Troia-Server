package com.datascience.datastoring.transforms;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.PureNominalData;
import com.datascience.core.results.*;
import com.datascience.serialization.ISerializer;
import com.datascience.serialization.SerializationTransform;
import com.datascience.serialization.json.JSONUtils;
import com.datascience.utils.ITransformation;
import com.google.common.reflect.TypeToken;

import java.util.Collection;

/**
 * @Author: konrad
 */
public class SerializerBasedCoreTransformsFactory extends SingletonsStringCoreTransformsFactory {

	protected ISerializer serializer;

	public SerializerBasedCoreTransformsFactory(ISerializer serializer){
		this.serializer = serializer;
	}

	@Override
	public ITransformation<Collection<AssignedLabel<String>>, String> createNominalAssignsTransformation() {
		return new SerializationTransform<Collection<AssignedLabel<String>>>(serializer,
				JSONUtils.assignsStringType);
	}

	@Override
	public ITransformation<Collection<LObject<String>>, String> createNominalObjectsTransformation() {
		return new SerializationTransform<Collection<LObject<String>>>(serializer,
				JSONUtils.objectsStringType);
	}

	@Override
	public ITransformation<Collection<AssignedLabel<ContValue>>, String> createContAssignsTransformation() {
		return new SerializationTransform<Collection<AssignedLabel<ContValue>>>(serializer,
				JSONUtils.assignsContValueType);
	}

	@Override
	public ITransformation<Collection<LObject<ContValue>>, String> createContObjectsTransformation() {
		return new SerializationTransform<Collection<LObject<ContValue>>>(serializer,
				JSONUtils.objectsContValueType);
	}

	@Override
	public ITransformation<Collection<Worker>, String> createWorkersTransformation() {
		return new SerializationTransform<Collection<Worker>>(serializer,
				new TypeToken<Collection<Worker>>(){}.getType()); // TODO XXX FIXME
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
