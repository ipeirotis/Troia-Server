package com.datascience.serialization.json;

import com.datascience.core.base.Category;
import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.results.DatumResult;
import com.datascience.core.results.Results;
import com.datascience.core.results.ResultsFactory;
import com.datascience.core.results.WorkerResult;
import com.datascience.scheduler.AssignCountPriorityCalculator;
import com.datascience.scheduler.CachedScheduler;
import com.datascience.scheduler.IPriorityCalculator;
import com.datascience.scheduler.Scheduler;
import com.datascience.utils.ProbabilityDistributions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ResultsTest {

	private Gson gson;

	public ResultsTest() {
		GsonBuilder builder = JSONUtils.getFilledDefaultGsonBuilder();
		gson = builder.create();
	}

	@Test
	public void schedulerSerializationTest() {
		ArrayList<Category> categories = new ArrayList<Category>();
		Category category1 = new Category("category1");
		Category category2 = new Category("category2");
		categories.add(category1);
		categories.add(category2);

		Results<String, DatumResult, WorkerResult> results = NominalProject.createResultsInstance(categories);
		LObject<String> obj = new LObject<String>("obj");
		obj.setEvaluationLabel("category2");
		DatumResult dr = results.getOrCreateDatumResult(obj);
		Map<String, Double> categoryProb = dr.getCategoryProbabilites();
		categoryProb.put("category1", 0.3);
		categoryProb.put("category2", 0.7);
		String serialized = gson.toJson(results);

		Results<String, DatumResult, WorkerResult> deserialized = gson.fromJson(serialized, new TypeToken<Results<String, DatumResult, WorkerResult>>(){}.getType());
		Assert.assertEquals(deserialized.getDatumCreator().getClass(), results.getDatumCreator().getClass());
		Assert.assertEquals(deserialized.getWorkerCreator().getClass(), results.getWorkerCreator().getClass());
		Assert.assertNotNull(deserialized.getDatumResult(obj));
		DatumResult ddr = deserialized.getDatumResult(obj);
		Assert.assertEquals(ddr.getCategoryProbabilites(), dr.getCategoryProbabilites());
		Assert.assertEquals(gson.toJson(deserialized), serialized);
	}
}
