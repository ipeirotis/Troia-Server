package com.datascience.gal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import java.util.ArrayList;

public class BatchDawidSkeneTest {



	@Test
	public final void testAddLabelWithWrongCategory() {
		Category category1 = new Category("category1");
		Category category2 = new Category("category2");
		ArrayList<Category> categories = new ArrayList<Category>();
		categories.add(category1);
		categories.add(category2);
		BatchDawidSkene ds = new BatchDawidSkene("id",categories);
		AssignedLabel correctLabel = new AssignedLabel("worker","object1","category1");
		AssignedLabel incorrectLabel = new AssignedLabel("worker","object2","wrongLabel");
		ds.addAssignedLabel(correctLabel);
		try {
			ds.addAssignedLabel(incorrectLabel);
			fail("Added label with incorrect category.");
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		assertEquals(ds.getNumberOfObjects(),1);
	}

}
