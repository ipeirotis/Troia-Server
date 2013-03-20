package com.datascience.core;

import com.datascience.gal.BatchDawidSkene;
import com.datascience.gal.IncrementalDawidSkene;
import com.datascience.galc.ContinuousIpeirotis;
import com.datascience.mv.BatchMV;
import com.datascience.mv.IncrementalMV;
import com.datascience.service.GSONSerializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


/**
 * @Author: artur
 */
public class JobFactoryTest {


	private JsonArray createCategoriesJsonArray(){
		JsonArray cat = new JsonArray();
		JsonObject cat1 = new JsonObject();
		cat1.addProperty("name", "cat1");
		JsonObject cat2 = new JsonObject();
		cat2.addProperty("name", "cat1");
		cat.add(cat1);
		cat.add(cat2);
		return cat;
	}

	private void checkProjectDependecies(Job job, Class clazz){
		assertNotNull(job.getProject());
		assertNotNull(job.getProject().getAlgorithm());
		assertNotNull(job.getProject().getData());
		assertNotNull(job.getProject().getResults());
		assertNotNull(job.getProject().getScheduler());
		assertEquals(job.getProject().getAlgorithm().getClass(), clazz);

		assertSame(job.getProject().getData(), job.getProject().getAlgorithm().getData());
		assertSame(job.getProject().getResults(), job.getProject().getAlgorithm().getResults());

		assertSame(job.getProject().getData(), job.getProject().getScheduler().getData());
	}

	@Test
	public void createNominalJob() throws Exception {
		HashMap<String, Class> alg = new HashMap<String, Class>();
		alg.put("BDS", BatchDawidSkene.class);
		alg.put("IDS", IncrementalDawidSkene.class);
		alg.put("BMV", BatchMV.class);
		alg.put("IMV", IncrementalMV.class);
		for (Map.Entry<String, Class> e : alg.entrySet()){
			JobFactory jf = new JobFactory(new GSONSerializer());
			JsonObject jo = new JsonObject();
			jo.addProperty("algorithm", e.getKey());
			jo.add("categories", createCategoriesJsonArray());

			Job job = jf.createNominalJob(jo, "test");
			checkProjectDependecies(job, e.getValue());
		}
	}

	@Test
	public void createContinuousJob() throws Exception {
		JobFactory jf = new JobFactory(new GSONSerializer());
		JsonObject jo = new JsonObject();
		Job job = jf.createContinuousJob(jo, "test");
		checkProjectDependecies(job, ContinuousIpeirotis.class);
	}
}
