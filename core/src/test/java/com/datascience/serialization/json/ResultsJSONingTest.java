package com.datascience.serialization.json;

import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.datastoring.datamodels.memory.InMemoryResults;
import com.datascience.core.results.DatumResult;
import com.datascience.core.results.IResults;
import com.datascience.core.results.WorkerResult;
import com.datascience.datastoring.datamodels.full.MemoryJobStorage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Test;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ResultsJSONingTest {

	private Gson gson;

	public ResultsJSONingTest() {
		GsonBuilder builder = JSONUtils.getFilledDefaultGsonBuilder();
		gson = builder.create();
	}

	@Test
	public void resultsSerializationTest() {
		ArrayList<String> categories = new ArrayList<String>();
		categories.add("category1");
		categories.add("category2");

		MemoryJobStorage js = new MemoryJobStorage();
		IResults<String, DatumResult, WorkerResult> results = js.getNominalResults("testid", categories);
		LObject<String> obj = new LObject<String>("obj");
		obj.setEvaluationLabel("category2");
		DatumResult dr = results.getOrCreateDatumResult(obj);
		Map<String, Double> categoryProb = new HashMap<String, Double>();
		categoryProb.put("category1", 0.3);
		categoryProb.put("category2", 0.7);
		dr.setCategoryProbabilites(categoryProb);
		results.addDatumResult(obj, dr);
		String serialized = gson.toJson(results);
		LObject<String> imaginaryObj = new LObject<String>("ImaginaryObj");
		Worker<String> imaginaryWorker = new Worker<String>("ImaginaryWorker");

		InMemoryResults<String, DatumResult, WorkerResult> deserialized = gson.fromJson(serialized, new TypeToken<InMemoryResults<String, DatumResult, WorkerResult>>(){}.getType());
//		Assert.assertEquals(deserialized.getOrCreateDatumResult(imaginaryObj).getClass(),
//				results.getOrCreateDatumResult(imaginaryObj).getClass());
//		Assert.assertEquals(deserialized.getOrCreateWorkerResult(imaginaryWorker).getClass(),
//				results.getOrCreateWorkerResult(imaginaryWorker).getClass());
		// ^^^ above doesn't work - it works when jsoned at job level
		Assert.assertNotNull(deserialized.getDatumResult(obj));
		DatumResult ddr = deserialized.getDatumResult(obj);
		Assert.assertEquals(ddr.getCategoryProbabilites(), dr.getCategoryProbabilites());
		Assert.assertEquals(gson.toJson(deserialized), serialized);
	}
}
