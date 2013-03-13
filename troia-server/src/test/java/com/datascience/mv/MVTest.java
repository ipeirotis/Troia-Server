package com.datascience.mv;

import com.datascience.core.base.*;
import com.datascience.core.base.Category;
import com.datascience.core.nominal.NominalData;
import com.datascience.core.results.Results;
import com.datascience.core.results.DatumResult;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.results.WorkerResult;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;

/**
 * @Author: konrad
 */
public class MVTest {

	protected ArrayList<Category> categories;
	protected ArrayList<Worker<String>> workers;
	protected ArrayList<LObject<String>> objects;
	protected ArrayList<LObject<String>> goldObjects;
	protected ArrayList<AssignedLabel<String>> assigns;
	int nWorkers = 3, nObjects = 4, nGold = 1;

	@Before
	public void setUp(){
		int nAssigns = nWorkers * (nObjects + nGold);
		int i;
		categories = new ArrayList<Category>();
		categories.add(new Category("AAA"));
		categories.add(new Category("BBB"));
		workers = new ArrayList<Worker<String>>();
		for (i=0;i<nWorkers;i++) {
			workers.add(new Worker<String>("worker" + i));
		}
		objects = new ArrayList<LObject<String>>();
		for (i=0;i<nObjects;i++) {
			objects.add(new LObject<String>("object" + i));
		}
		goldObjects = new ArrayList<LObject<String>>();
		for (i=0;i<nGold;i++) {
			LObject<String> gold = new LObject<String>("gObject" + i);
			gold.setGoldLabel("AAA");
			goldObjects.add(gold);
		}
		objects.addAll(goldObjects);
		assigns = new ArrayList<AssignedLabel<String>>();
		for (i=0;i<nAssigns;i++) {
			assigns.add(new AssignedLabel<String>(workers.get(i % nWorkers),
					objects.get(i % (nObjects + nGold)),
					categories.get(i % categories.size()).getName()));
		}
	}


	protected void fillNominalData(NominalData data){
		data.addCategories(categories);
		for (LObject<String> gold: goldObjects){
			data.addGoldObject(gold);
		}
		for (AssignedLabel<String> assign: assigns) {
			data.addAssign(assign);
		}
	}

	protected void testMVResults(Results<String, DatumResult, WorkerResult> results){
		double eps = 0.000001;
		double[] exp = new double[]{2./3, 1./3};
		for (int i=0;i<nObjects;i++){
			LObject<String> object = objects.get(i);
			DatumResult dr = results.getDatumResult(object);
			Map<String, Double> pd = dr.getCategoryProbabilites();
			int ii = i % 2;
			assertEquals(exp[ii], pd.get("AAA"), eps);
			assertEquals(exp[1-ii], pd.get("BBB"), eps);
		}

		for (int i=0;i<nGold;i++){
			LObject<String> object = goldObjects.get(i);
			DatumResult dr = results.getDatumResult(object);
			Map<String, Double> pd = dr.getCategoryProbabilites();
			assertEquals(1., pd.get("AAA"), eps);
			assertEquals(0., pd.get("BBB"), eps);
		}
	}

	@Test
	public void testBatchMV(){
		BatchMV mv = new BatchMV();
		NominalProject np = new NominalProject(mv);
		NominalData nd = np.getData();
		Results<String, DatumResult, WorkerResult> results = np.getResults();
		mv.setData(nd);
		mv.setResults(results);
		fillNominalData(nd);
		mv.compute();
		testMVResults(results);
	}

	@Test
	public void testIncrementalMV(){
		IncrementalMV mv = new IncrementalMV();
		NominalProject np = new NominalProject(mv);
		NominalData nd = np.getData();
		Results<String, DatumResult, WorkerResult> results = np.getResults();
		mv.setData(nd);
		mv.setResults(results);
		nd.addNewUpdatableAlgorithm(mv);
		fillNominalData(nd);
		testMVResults(results);
	}
}
