/**
 *
 */
package com.datascience.gal;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.datascience.gal.IncrementalDawidSkene.IncrementalDawidSkeneDeserializer;
import com.datascience.gal.service.JSONUtils;
import com.google.gson.JsonObject;

/**
 * @author Michael Arshynov
 *
 */
@Ignore
public class IncrementalDawidSkeneDeserializerTest extends
	IncrementalDawidSkeneDeserializer {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.datascience.gal.IncrementalDawidSkene.IncrementalDawidSkeneDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)}.
	 */
	@Test
	public final void testDeserialize() {
		String id = "id";
		Map<String, Datum> objects = null;
		Map<String, Worker> workers = null;
		Map<String, Category> categories = null;
		boolean fixedPriors = false;
		double priorDenominator = 1e1;
//		Type typeOfSrc = new TypeToken<Map<String, Datum>>(){}.getType();
//		JSONUtils.gson.to
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", id);
		jsonObject.add("objects", JSONUtils.gson.toJsonTree(objects, JSONUtils.stringDatumMapType));
		jsonObject.add("workers", JSONUtils.gson.toJsonTree(workers, JSONUtils.strinWorkerMapType));
		jsonObject.add("categories", JSONUtils.gson.toJsonTree(categories, JSONUtils.stringCategoryMapType));
		jsonObject.addProperty("fixedPriors", fixedPriors);
		jsonObject.addProperty("dsmethod", IncrementalDSMethod.ITERATELOCAL.toString());
		jsonObject.addProperty("priorDenominator", priorDenominator);

		Object o = this.deserialize(jsonObject, null, null);
		assertTrue(o instanceof IncrementalDawidSkene);
	}

}
