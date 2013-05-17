package com.datascience.utils.transformations;

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

	public static interface ITransformationCreator{
		ITransformation<Collection<AssignedLabel<String>>, String> createStringAssignsTransformation();
		ITransformation<Collection<LObject<String>>, String> createStringObjectsTransformation();

		ITransformation<Collection<AssignedLabel<ContValue>>, String> createContAssignsTransformation();
		ITransformation<Collection<LObject<ContValue>>, String> createContObjectsTransformation();

		ITransformation<Collection<Worker>, String> createWorkersTransformation();

		ITransformation<DatumContResults, String> createDatumContResultsTransformation();
		ITransformation<WorkerContResults, String> createWorkerContResultsTransformation();
		ITransformation<DatumResult, String> createDatumStringResultsTransformation();
		ITransformation<WorkerResult, String> createWorkerStringResultsTransformation(ResultsFactory.WorkerResultNominalFactory wrnf);
	}

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
		private String objectSeparator = "|";
		private String collectionSeparator = "$";
		private String mapSeparator = ";";

		StringTransform stringTransform = new StringTransform();
		ContValueTransform contValueTransform = new ContValueTransform(objectSeparator);

		@Override
		public ITransformation<Collection<AssignedLabel<String>>, String> createStringAssignsTransformation() {
			AssignTransform<String> assignTransform = new AssignTransform<String>(objectSeparator, stringTransform);
			return new CollectionTransform<AssignedLabel<String>>(collectionSeparator, assignTransform);
		}

		@Override
		public ITransformation<Collection<LObject<String>>, String> createStringObjectsTransformation() {
			LObjectTransform<String> objectTransform = new LObjectTransform<String>(objectSeparator, stringTransform);
			return new CollectionTransform<LObject<String>>(collectionSeparator, objectTransform);
		}

		@Override
		public ITransformation<Collection<Worker>, String> createWorkersTransformation() {
			WorkerTransform workerTransform = new WorkerTransform();
			return new CollectionTransform<Worker>(collectionSeparator, workerTransform);
		}

		@Override
		public ITransformation<Collection<AssignedLabel<ContValue>>, String> createContAssignsTransformation() {
			AssignTransform<ContValue> assignTransform = new AssignTransform<ContValue>(objectSeparator, contValueTransform);
			return new CollectionTransform(collectionSeparator, assignTransform);
		}

		@Override
		public ITransformation<Collection<LObject<ContValue>>, String> createContObjectsTransformation() {
			LObjectTransform<ContValue> objectTransform = new LObjectTransform<ContValue>(objectSeparator, contValueTransform);
			return new CollectionTransform(collectionSeparator, objectTransform);
		}

		@Override
		public ITransformation<DatumContResults, String> createDatumContResultsTransformation() {
			return new DatumContResultTransform(objectSeparator);
		}

		@Override
		public ITransformation<WorkerContResults, String> createWorkerContResultsTransformation() {
			AssignTransform<ContValue> assignTransform = new AssignTransform<ContValue>(objectSeparator, contValueTransform);
			CollectionTransform<AssignedLabel<ContValue>> assignCollectionTransform = new CollectionTransform(collectionSeparator, assignTransform);
			return new WorkerContResultTransform(objectSeparator, assignCollectionTransform);
		}

		@Override
		public ITransformation<DatumResult, String> createDatumStringResultsTransformation() {
			return new DatumResultTransform(mapSeparator);
		}

		@Override
		public ITransformation<WorkerResult, String> createWorkerStringResultsTransformation(ResultsFactory.WorkerResultNominalFactory wrnf) {
			return new WorkerResultTransform(wrnf);
		}
	}

	static Map<String, ITransformationCreator> TRANSFORMATION_FACTORY = new HashMap();
	static {
		TRANSFORMATION_FACTORY.put("STRING", new StringTransformationCreator());
		TRANSFORMATION_FACTORY.put("GSON", new SerializationBasedTransformationCreator(new GSONSerializer()));
	}

	public static ITransformationCreator create(String type){
		ITransformationCreator tc = TRANSFORMATION_FACTORY.get(type.toUpperCase());
		checkArgument(tc != null, "Unknown transformation type: " + type);
		return tc;
	}
}
