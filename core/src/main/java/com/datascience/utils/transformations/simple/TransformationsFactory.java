package com.datascience.utils.transformations.simple;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.results.*;
import com.datascience.serialization.ISerializer;
import com.datascience.serialization.SerializationTransform;
import com.datascience.serialization.json.GSONSerializer;
import com.datascience.serialization.json.JSONUtils;
import com.datascience.utils.ITransformation;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * User: artur
 * Date: 5/16/13
 */
public class TransformationsFactory {

	public static class SerializationBasedTransformationCreator implements ITransformationCreator{

		ISerializer serializer;
		public SerializationBasedTransformationCreator(ISerializer serializer){
			this.serializer = serializer;
		}

		@Override
		public ITransformation<Collection<AssignedLabel<String>>, String> createStringAssignsTransformation(){
			return new SerializationTransform<Collection<AssignedLabel<String>>>(serializer, JSONUtils.assignsStringType);
		}

		@Override
		public ITransformation<Collection<LObject<String>>, String> createStringObjectsTransformation() {
			return new SerializationTransform<Collection<LObject<String>>>(serializer, JSONUtils.objectsStringType);
		}

		@Override
		public ITransformation<Collection<AssignedLabel<ContValue>>, String> createContAssignsTransformation() {
			return new SerializationTransform<Collection<AssignedLabel<ContValue>>>(serializer, JSONUtils.assignsContValueType);
		}

		@Override
		public ITransformation<Collection<LObject<ContValue>>, String> createContObjectsTransformation() {
			return new SerializationTransform<Collection<LObject<ContValue>>>(serializer, JSONUtils.objectsContValueType);
		}

		@Override
		public ITransformation<Collection<Worker>, String> createWorkersTransformation() {
			return new SerializationTransform<Collection<Worker>>(serializer, JSONUtils.workersCollection);
		}

		@Override
		public ITransformation<DatumContResults, String> createDatumContResultsTransformation() {
			return new SerializationTransform<DatumContResults>(serializer, DatumContResults.class);
		}

		@Override
		public ITransformation<WorkerContResults, String> createWorkerContResultsTransformation() {
			return  new SerializationTransform<WorkerContResults>(serializer, WorkerContResults.class);
		}

		@Override
		public ITransformation<DatumResult, String> createDatumStringResultsTransformation() {
			return new SerializationTransform<DatumResult>(serializer, DatumResult.class);
		}

		@Override
		public ITransformation<WorkerResult, String> createWorkerStringResultsTransformation(ResultsFactory.WorkerResultNominalFactory wrnf) {
			return new SerializationTransform<WorkerResult>(serializer, WorkerResult.class);
		}
	}

	public static class StringTransformationCreator implements ITransformationCreator {
	}

	static Map<String, ITransformationCreator> TRANSFORMATION_FACTORY = new HashMap();
	static {
		TRANSFORMATION_FACTORY.put("SIMPLE", new StringTransformationCreator());
		TRANSFORMATION_FACTORY.put("JSON", new SerializationBasedTransformationCreator(new GSONSerializer()));
	}

	public static ITransformationCreator create(String type){
		ITransformationCreator tc = TRANSFORMATION_FACTORY.get(type.toUpperCase());
		checkArgument(tc != null, "Unknown transformation type: " + type);
		return tc;
	}
}
