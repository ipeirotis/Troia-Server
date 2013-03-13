package com.datascience.core.storages;

import com.datascience.core.base.*;
import com.datascience.serialization.ISerializer;
import com.datascience.service.GSONSerializer;
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

		ISerializer serializer = new GSONSerializer();
		String s = serializer.serialize(data);
		System.out.println(s);
		Data<ContValue> d = serializer.parse(s, Data.class);
		String s2 = serializer.serialize(d);
		assertEquals(s, s2);

		cdc.assertEqual(d);
	}
}
