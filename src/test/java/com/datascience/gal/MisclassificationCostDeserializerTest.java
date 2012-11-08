/**
 *
 */
package com.datascience.gal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.datascience.gal.MisclassificationCost.MisclassificationCostDeserializer;
import com.datascience.utils.auxl.TestDataManager;
import com.google.gson.JsonObject;

/**
 * @author Michael Arshynov
 *
 */
public class MisclassificationCostDeserializerTest extends
	MisclassificationCostDeserializer {

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
	 * Test method for {@link com.datascience.gal.MisclassificationCost.MisclassificationCostDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)}.
	 */
	@Test
	public final void testDeserialize() {
		String from = "category1";
		String to = "category2";
		double cost = 0.1;
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("categoryFrom", from);
		jsonObject.addProperty("categoryTo", to);
		jsonObject.addProperty("cost", 0.1);
		Object o = this.deserialize(jsonObject, null, null);
		assertTrue(o instanceof MisclassificationCost);
		assertEquals(from, ((MisclassificationCost) o).getCategoryFrom());
		assertEquals(to, ((MisclassificationCost) o).getCategoryTo());
		assertEquals(cost, ((MisclassificationCost) o).getCost(), TestDataManager.DELTA_DOUBLE);
	}

}
