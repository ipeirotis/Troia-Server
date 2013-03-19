package com.datascience.core.storages;

import com.datascience.core.base.*;
import com.datascience.core.results.DatumResult;
import com.datascience.core.results.Results;
import com.datascience.core.results.WorkerResult;
import com.datascience.serialization.ISerializer;
import com.datascience.serialization.json.JSONUtils;
import com.datascience.service.GSONSerializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @Author: konrad
 */
public class TestCoreBaseSerialization {

	@Test
	public void testContData(){
		ContDataComparator cdc = new ContDataComparator();
		Data<ContValue> data = cdc.getContData();

		JSONUtils ju = new JSONUtils();
		Gson gson = JSONUtils.getFilledDefaultGsonBuilder().create();
		String s = gson.toJson(data);
		System.out.println(s);
		Data<ContValue> d = gson.fromJson(s, new TypeToken<Data<ContValue>>(){}.getType());
		String s2 = gson.toJson(d);
		assertEquals(s, s2);
		cdc.assertEqual(d);
	}
}
