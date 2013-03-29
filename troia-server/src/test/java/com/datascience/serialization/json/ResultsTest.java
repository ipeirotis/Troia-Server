package com.datascience.serialization.json;

import com.datascience.core.base.LObject;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.results.DatumResult;
import com.datascience.core.results.Results;
import com.datascience.core.results.WorkerResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultsTest {

	private Gson gson;

	public ResultsTest() {
		GsonBuilder builder = JSONUtils.getFilledDefaultGsonBuilder();
		gson = builder.create();
	}

	@Test
	public void resultsSerializationTest() {
		ArrayList<String> categories = new ArrayList<String>();
		categories.add("category1");
		categories.add("category2");

		Results<String, DatumResult, WorkerResult> results = NominalProject.createResultsInstance(categories);
		LObject<String> obj = new LObject<String>("obj");
		obj.setEvaluationLabel("category2");
		DatumResult dr = results.getOrCreateDatumResult(obj);
		Map<String, Double> categoryProb = new HashMap<String, Double>();
		categoryProb.put("category1", 0.3);
		categoryProb.put("category2", 0.7);
		dr.setCategoryProbabilites(categoryProb);
		results.addDatumResult(obj, dr);
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
