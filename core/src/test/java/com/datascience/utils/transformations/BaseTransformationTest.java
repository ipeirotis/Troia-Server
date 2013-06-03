package com.datascience.utils.transformations;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.results.*;
import com.datascience.datastoring.transforms.ICoreTransformsFactory;
import com.datascience.utils.ITransformation;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

/**
 * User: artur
 * Date: 5/16/13
 */
public abstract class BaseTransformationTest {

	protected abstract ICoreTransformsFactory<String> getCreator();

	private abstract static class LabelCreator<T>{
		abstract T create(int k);

	}
	private static class StringLabelCreator extends LabelCreator<String> {
		String create (int k){
			return "label" + k;
		}

	}
	private static class ContValueLabelCreator extends LabelCreator<ContValue>{
		ContValue create(int k){
			return new ContValue(k*1., k*1.);
		}

	}

	private <T> Collection<AssignedLabel<T>> createAssignedLabels(int k, LabelCreator<T> labelCreator){
		Collection<AssignedLabel<T>> labels = new LinkedList<AssignedLabel<T>>();
		for (int i = 0; i< k; i++){
			labels.add(new AssignedLabel<T>(new Worker<T>("worker"+i), new LObject<T>("object"+i), labelCreator.create(i)));
		}
		return labels;
	}

	private <T> Collection<LObject<T>> createLObjects(int k, LabelCreator<T> labelCreator, boolean gold, boolean evaluation){
		Collection<LObject<T>> objects = new LinkedList<LObject<T>>();
		for (int i =0; i<k; i++){
			LObject<T> obj = new LObject<T>("object"+i);
			if (gold)
				obj.setGoldLabel(labelCreator.create(i));
			if (evaluation)
				obj.setEvaluationLabel(labelCreator.create(i));
			objects.add(obj);
		}
		return objects;
	}

	private Collection<Worker> createWorkers(int k){
		Collection<Worker> workers = new LinkedList<Worker>();
		for (int i =0; i<k; i++){
			workers.add(new Worker("worker"+i));
		}
		return  workers;
	}

	private <T> void testAssignsTransformation(LabelCreator<T> creator, ITransformation<Collection<AssignedLabel<T>>, String> transformation){
		Collection<AssignedLabel<T>> labels = this.<T>createAssignedLabels(5, creator);
		ArrayList<AssignedLabel<T>> transformedLabels = new ArrayList<AssignedLabel<T>>(transformation.inverse(transformation.transform(labels)));
		Assert.assertEquals(transformedLabels.size(), labels.size());
		int i = 0;
		for (AssignedLabel<T> al : labels){
			AssignedLabel<T> al2 = transformedLabels.get(i);
			Assert.assertEquals(al, al2);
			Assert.assertEquals(al.getLobject(), al2.getLobject());
			Assert.assertEquals(al.getWorker(), al2.getWorker());
			i++;
		}
	}

	@Test
	public void testStringAssignsTransformation(){
		testAssignsTransformation(new StringLabelCreator(), getCreator().<String>createAssignsTransformation());
	}

	@Test
	public void testContValueAssignsTransformation(){
		testAssignsTransformation(new ContValueLabelCreator(), getCreator().<ContValue>createAssignsTransformation());
	}

	private <T> void testObjectsTransformation(LabelCreator<T> creator, ITransformation<Collection<LObject<T>>, String> transformation){
		for (boolean gold : new boolean[]{true, false}){
			for (boolean eval : new boolean[]{true, false}){
				Collection<LObject<T>> objects = this.<T>createLObjects(5, creator, gold, eval);
				ArrayList<LObject<T>> transformedObjects = new ArrayList<LObject<T>>(transformation.inverse(transformation.transform(objects)));
				Assert.assertEquals(transformedObjects.size(), objects.size());
				int i = 0;
				for (LObject<T> obj : objects){
					LObject<T> obj2 = transformedObjects.get(i);
					Assert.assertEquals(obj, obj2);
					if (gold)
						Assert.assertEquals(obj.getGoldLabel(), obj2.getGoldLabel());
					if (eval)
						Assert.assertEquals(obj.getEvaluationLabel(), obj2.getEvaluationLabel());
					i++;
				}
			}
		}
	}

	@Test
	public void testStringObjectsTransformation(){
		testObjectsTransformation(new StringLabelCreator(), getCreator().<String>createObjectsTransformation());
	}

	@Test
	public void testContValueObjectsTransformation(){
		testObjectsTransformation(new ContValueLabelCreator(), getCreator().<ContValue>createObjectsTransformation());
	}

	@Test
	public void testWorkersTransformation(){
		Collection<Worker> workers = createWorkers(5);
		ITransformation<Collection<Worker>, String> transformation = getCreator().createWorkersTransformation();
		ArrayList<Worker> transformedWorkers = new ArrayList<Worker>(transformation.inverse(transformation.transform(workers)));
		Assert.assertEquals(workers.size(), transformedWorkers.size());
		int i =0;
		for (Worker w : workers){
			Worker w2 = transformedWorkers.get(i);
			Assert.assertEquals(w, w2);
			i++;
		}
	}

	@Test
	public void testWorkersResultsTransformation(){
		ResultsFactory.WorkerResultNominalFactory creator = new ResultsFactory.WorkerResultNominalFactory();
		creator.setCategories(Lists.newArrayList(new String[]{"Cat1", "Cat2"}));
		WorkerResult wr = creator.create();
		ITransformation<WorkerResult, String> transformation = getCreator().createWorkerStringResultsTransformation(creator);
		WorkerResult transformedWR = transformation.inverse(transformation.transform(wr));
		Assert.assertEquals(wr, transformedWR);
	}

	@Test
	public void testWorkersContResultsTransformation(){
		ResultsFactory.WorkerContResultFactory creator = new ResultsFactory.WorkerContResultFactory();
		WorkerContResults wr = creator.create();
		wr.setEst_sigma(0.0);
		wr.setEst_mu(0.1);
		wr.setEst_rho(0.2);
		wr.setTrueSigma(0.3);
		wr.setTrueMu(0.4);
		wr.setTrueRho(0.5);
		wr.setZetaValue(createAssignedLabels(3, new ContValueLabelCreator()));
		ITransformation<WorkerContResults, String> transformation = getCreator().createWorkerContResultsTransformation();
		WorkerContResults transformedWR = transformation.inverse(transformation.transform(wr));
		Assert.assertEquals(wr, transformedWR);
	}

	@Test
	public void testDatumResultTransformation(){
		ResultsFactory.DatumResultFactory creator = new ResultsFactory.DatumResultFactory();
		DatumResult dr = creator.create();
		Map<String, Double> cp = new HashMap<String, Double>();
		cp.put("Cat1", 0.4);
		cp.put("Cat2", 0.6);
		dr.setCategoryProbabilites(cp);
		ITransformation<DatumResult, String> transformation = getCreator().createDatumStringResultsTransformation();
		DatumResult transformedDR = transformation.inverse(transformation.transform(dr));
		Assert.assertEquals(dr.getCategoryProbabilites(), transformedDR.getCategoryProbabilites());
	}

	@Test
	public void testDatumContResultTransformation(){
		ResultsFactory.DatumContResultFactory creator = new ResultsFactory.DatumContResultFactory();
		DatumContResults dr = creator.create();
		ITransformation<DatumContResults, String> transformation = getCreator().createDatumContResultsTransformation();
		DatumContResults transformedDR = transformation.inverse(transformation.transform(dr));
		Assert.assertEquals(dr, transformedDR);
	}
}
