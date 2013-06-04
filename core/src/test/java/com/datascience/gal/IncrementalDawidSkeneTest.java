/**
 *
 */
package com.datascience.gal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import com.datascience.core.base.LObject;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.base.Worker;
import com.datascience.datastoring.datamodels.full.MemoryJobStorage;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.datascience.utils.auxl.TestDataManager;

public class IncrementalDawidSkeneTest {

	NominalProject project;
	ArrayList<String> categories;

	@Before
	public void setUp(){
		categories = new ArrayList<String>();
		categories.add("category1");
		categories.add("category2");

		IncrementalDawidSkene alg = new IncrementalDawidSkene();
		MemoryJobStorage js = new MemoryJobStorage();
		project = new NominalProject(alg, js.getNominalData("testId"), js.getNominalResults("testId", categories));
		project.getData().addNewUpdatableAlgorithm(alg);
		project.initializeCategories(categories, null, null);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testInitializePriors() {
		double actual = project.getAlgorithm().prior(categories.get(0));
		double expected = 1. / project.getData().getCategories().size();
		assertEquals(expected, actual, TestDataManager.DELTA_DOUBLE);
	}

	@Test
	public final void testGetErrorRateForWorker() {
		Worker w = new Worker("worker1");
		project.getData().addWorker(w);
		double errorRate = ((AbstractDawidSkene)project.getAlgorithm()).getErrorRateForWorker(
				w,
				categories.get(0),
				categories.get(1));
		assertTrue(errorRate>=0 && errorRate<=1);
	}

	@Test
	public final void testGetObjectClassProbabilites(){
		LObject<String> obj = new LObject<String>("object");
		project.getData().addObject(obj);
		for (Double val : ((AbstractDawidSkene)project.getAlgorithm()).getObjectClassProbabilities(obj).values()){
			assertEquals(0.0, val, TestDataManager.DELTA_DOUBLE);
		}

		LObject<String> gold = new LObject<String>("gold_object");
		gold.setGoldLabel("category1");
		project.getData().addObject(gold);
		Map<String, Double> cp = ((AbstractDawidSkene)project.getAlgorithm()).getObjectClassProbabilities(gold);
		assertEquals(1., cp.get("category1"), TestDataManager.DELTA_DOUBLE);
		assertEquals(0., cp.get("category2"), TestDataManager.DELTA_DOUBLE);
	}


	private boolean compareHashSets(HashSet s1, HashSet s2) {
		if(s1== null && s2==null)
			return true;
		if(s1 != null && s2 != null)
			return s1.containsAll(s2) && s2.containsAll(s1);
		else
			return false;
	}

	private <T1, T2> boolean compareHashMaps(Map<T1, T2> m1, Map<T1, T2> m2) {
		if(m1== null && m2==null)
			return true;
		if(m1 != null && m2 != null) {
			for (Entry<T1, T2> entry : m1.entrySet()) {
				T1 key = entry.getKey();
				if  (!entry.getValue().equals(m2.get(key)))
					return false;
			}
			return true;
		} else return false;
	}
}
