package com.datascience.core;

import com.datascience.gal.BatchDawidSkene;
import com.datascience.gal.IncrementalDawidSkene;
import com.datascience.galc.ContinuousIpeirotis;
import com.datascience.mv.BatchMV;
import com.datascience.mv.IncrementalMV;
import com.datascience.service.GSONSerializer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
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
		cat.add(new JsonPrimitive("cat1"));
		cat.add(new JsonPrimitive("cat2"));
		return cat;
	}

	private void checkProjectDependecies(Job job, Class clazz, boolean nominal){
		assertNotNull(job.getProject());
		assertNotNull(job.getProject().getAlgorithm());
		if (nominal)
			assertNotNull(job.getProject().getAlgorithm().getModel());
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
			jo.addProperty("scheduler", "scheduler");

			Job job = jf.createNominalJob(jo, "test");
			checkProjectDependecies(job, e.getValue(), true);
		}
	}

	@Test
	public void createContinuousJob() throws Exception {
		JobFactory jf = new JobFactory(new GSONSerializer());
		JsonObject jo = new JsonObject();
		jo.addProperty("scheduler", "scheduler");
		Job job = jf.createContinuousJob(jo, "test");
		checkProjectDependecies(job, ContinuousIpeirotis.class, false);
	}
}
