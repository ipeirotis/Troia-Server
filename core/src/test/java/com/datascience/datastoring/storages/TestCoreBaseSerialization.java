package com.datascience.datastoring.storages;

import com.datascience.core.base.*;
import com.datascience.datastoring.datamodels.memory.InMemoryData;
import com.datascience.serialization.json.JSONUtils;
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
		IData<ContValue> data = cdc.getContData();

		JSONUtils ju = new JSONUtils();
		Gson gson = JSONUtils.getFilledDefaultGsonBuilder().create();
		String s = gson.toJson(data);
		System.out.println(s);
		InMemoryData<ContValue> d = gson.fromJson(s, new TypeToken<InMemoryData<ContValue>>(){}.getType());
		String s2 = gson.toJson(d);
		assertEquals(s, s2);
		cdc.assertEqual(d);
	}
}
