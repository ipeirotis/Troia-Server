package com.datascience.datastoring.transforms;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.PureNominalData;
import com.datascience.core.results.*;
import com.datascience.utils.ITransformation;
import com.datascience.utils.transformations.simple.*;

import java.util.Collection;

public class SimpleStringCoreTransformsFactory extends SingletonsStringCoreTransformsFactory {

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
	public ITransformation<Collection<AssignedLabel<String>>, String> createNominalAssignsTransformation() {
		AssignTransform<String> assignTransform = new AssignTransform<String>(objectSeparator, stringTransform);
		return new CollectionTransform<AssignedLabel<String>>(collectionSeparator, assignTransform);
	}

	@Override
	public ITransformation<Collection<LObject<String>>, String> createNominalObjectsTransformation() {
		LObjectTransform<String> objectTransform = new LObjectTransform<String>(objectSeparator, stringTransform);
		return new CollectionTransform<LObject<String>>(collectionSeparator, objectTransform);
	}

	@Override
	public ITransformation<Collection<AssignedLabel<ContValue>>, String> createContAssignsTransformation() {
		AssignTransform<ContValue> assignTransform = new AssignTransform<ContValue>(objectSeparator, contValueTransform);
		return new CollectionTransform<AssignedLabel<ContValue>>(collectionSeparator, assignTransform);
	}

	@Override
	public ITransformation<Collection<LObject<ContValue>>, String> createContObjectsTransformation() {
		LObjectTransform<ContValue> objectTransform = new LObjectTransform<ContValue>(objectSeparator, contValueTransform);
		return new CollectionTransform<LObject<ContValue>>(collectionSeparator, objectTransform);
	}

	@Override
	public ITransformation<Collection<Worker>, String> createWorkersTransformation() {
		WorkerTransform workerTransform = new WorkerTransform();
		return new CollectionTransform<Worker>(collectionSeparator, workerTransform);
	}

	@Override
	public ITransformation<PureNominalData, String> createPureNominalDataTransformation() {
		return new NominalDataTransform();
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
