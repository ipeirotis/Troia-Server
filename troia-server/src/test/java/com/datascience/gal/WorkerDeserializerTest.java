/**
 *
 */
package com.datascience.gal;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.datascience.gal.Worker.WorkerDeserializer;
import com.datascience.core.storages.JSONUtils;
import com.google.gson.JsonObject;

/**
 * @author Michael Arshynov
 *
 */
public class WorkerDeserializerTest extends WorkerDeserializer {

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
	 * Test method for {@link com.datascience.gal.Worker.WorkerDeserializer#deserialize(com.google.gson.JsonElement, java.lang.reflect.Type, com.google.gson.JsonDeserializationContext)}.
	 */
	@Test
	public final void testDeserialize() {
		Category category1 = new Category("category1");
		Category category2 = new Category("category2");
		Category category3 = new Category("category3");
		List<Category> categoryList = new ArrayList<Category>();
		categoryList.add(category1);
		categoryList.add(category2);
		categoryList.add(category3);
		ConfusionMatrix conf = new MultinomialConfusionMatrix(categoryList);
		List<AssignedLabel> labels = new ArrayList<AssignedLabel>();
		AssignedLabel assignedLabel1 = new AssignedLabel("worker1", "datum1", "category1");
		AssignedLabel assignedLabel2 = new AssignedLabel("worker1", "datum2", "category2");
		AssignedLabel assignedLabel3 = new AssignedLabel("worker1", "datum3", "category3");
		labels.add(assignedLabel1);
		labels.add(assignedLabel2);
		labels.add(assignedLabel3);

		JsonObject jsonElement = new JsonObject();
		jsonElement.addProperty("name", "name1");
		jsonElement.add("cm", JSONUtils.gson.toJsonTree(conf, JSONUtils.confusionMatrixType));
		jsonElement.add("labels", JSONUtils.gson.toJsonTree(labels, JSONUtils.assignedLabelSetType));
		Object o = this.deserialize(jsonElement, null, null);
		assertTrue(o instanceof Worker);
	}

}
