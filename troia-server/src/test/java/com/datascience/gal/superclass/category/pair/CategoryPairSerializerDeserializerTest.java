/**
 *
 */
package com.datascience.gal.superclass.category.pair;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.datascience.gal.CategoryPair;
import com.google.gson.JsonElement;

/**
 * @author Michael Arshynov
 *
 */
public class CategoryPairSerializerDeserializerTest {

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
	 * Test method for {@link com.datascience.gal.CategoryPair.CategoryPairSerializer#serialize(com.datascience.gal.CategoryPair, java.lang.reflect.Type, com.google.gson.JsonSerializationContext)}.
	 * Test method for {@link com.datascience.gal.CategoryPair.CategoryPairDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)}
	 */
	@Test
	public final void testSerializeDeserialize() {
		CategoryPair categoryPair = new CategoryPair("source1", "dest1");

		CategoryPair.CategoryPairSerializer categoryPairSerializer = new CategoryPair.CategoryPairSerializer();
		JsonElement categoryPairSerialized = categoryPairSerializer.serialize(categoryPair, null, null);

		CategoryPair.CategoryPairDeserializer categoryPairDeserializer = new CategoryPair.CategoryPairDeserializer();
		CategoryPair categoryPairDeserialized = categoryPairDeserializer.deserialize(categoryPairSerialized, null, null);

		assertEquals(categoryPair, categoryPairDeserialized);
	}

}
