package com.datascience.datastoring.transforms;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.PureNominalData;
import com.datascience.core.results.*;
import com.datascience.utils.ITransformation;
import com.datascience.utils.Labels;
import com.datascience.utils.transformations.simple.*;

import java.util.Collection;

public class SimpleStringCoreTransformsFactory extends BaseStringCoreTransformsFactory{

	protected String objectSeparator;
	protected String collectionSeparator;
	protected String mapSeparator;

	StringTransform stringTransform;
	ContValueTransform contValueTransform;

	public SimpleStringCoreTransformsFactory(String objectSeparator, String collectionSeparator, String mapSeparator){
		this.objectSeparator = objectSeparator;
		this.collectionSeparator = collectionSeparator;
		this.mapSeparator = mapSeparator;

		stringTransform = new StringTransform();
		contValueTransform = new ContValueTransform(objectSeparator);
	}

	public SimpleStringCoreTransformsFactory(){
		this("|", "$", ";");
	}

	@Override
	public <T> ITransformation<Collection<AssignedLabel<T>>, String> createAssignsTransformation() {

		AssignTransform<T> assignTransform = new AssignTransform<T>(objectSeparator,
				Labels.<T, ITransformation>returnForKind(stringTransform, contValueTransform)); // TODO FIXME XXX drop need for labels
		return new CollectionTransform<AssignedLabel<T>>(collectionSeparator, assignTransform);
	}

	@Override
	public <T> ITransformation<Collection<LObject<T>>, String> createObjectsTransformation() {
		LObjectTransform<T> objectTransform = new LObjectTransform<T>(objectSeparator,
				Labels.<T, ITransformation>returnForKind(stringTransform, contValueTransform)); // TODO FIXME XXX drop need for labels
		return new CollectionTransform<LObject<T>>(collectionSeparator, objectTransform);	}

	@Override
	public <T> ITransformation<Collection<Worker<T>>, String> createWorkersTransformation() {
		WorkerTransform workerTransform = new WorkerTransform();
		return new CollectionTransform<Worker<T>>(collectionSeparator, workerTransform);
	}

	@Override
	public ITransformation<PureNominalData, String> createPureNominalDataTransformation() {
		return null;   // TODO FIXME XXX something should be here ...
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

	@Override
	public String getID() {
		return "SIMPLE";
	}

}
