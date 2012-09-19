/**
 * 
 */
package com.datascience.gal;

import static org.junit.Assert.assertTrue;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.datascience.gal.CorrectLabel.CorrectLabelDeserializer;
import com.google.gson.JsonObject;

/**
 * @author Michael Arshynov
 *
 */
public class CorrectLabelDeserializerTest extends CorrectLabelDeserializer {

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
	 * Test method for {@link com.datascience.gal.CorrectLabel.CorrectLabelDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)}.
	 */
	@Test
	public final void testDeserialize() {
		Object correctLabel;
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("objectName", "on1");
		jsonObject.addProperty("correctCategory", "cc1");
		
		correctLabel = this.deserialize(jsonObject, null, null);
		assertTrue(correctLabel instanceof CorrectLabel);
	}

}
