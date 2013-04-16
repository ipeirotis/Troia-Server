package com.datascience.serialization.json;

import com.datascience.core.jobs.Job;
import com.datascience.core.jobs.JobFactory;
import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.IncrementalNominalModel;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.storages.MemoryJobStorage;
import com.datascience.gal.IncrementalDawidSkene;
import com.datascience.service.GSONSerializer;
import com.google.gson.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * User: artur
 * Date: 3/27/13
 */
public class NominalModelTest {

	Random random = new Random();
	Gson gson;
	ArrayList<String> categories;

	public NominalModelTest() {
		GsonBuilder builder = JSONUtils.getFilledDefaultGsonBuilder();
		gson = builder.create();
	}

	private JsonArray createCategoriesJsonArray(ArrayList<String> categories){
		JsonArray cat = new JsonArray();
		for (String s : categories)
			cat.add(new JsonPrimitive(s));
		return cat;
	}

	private JsonArray createCategoryPriorsJsonArray(ArrayList<String> categories){
		JsonArray ja = new JsonArray();
		for (String s : categories){
			JsonObject cv = new JsonObject();
			cv.addProperty("categoryName", s);
			cv.addProperty("value", 1. / categories.size());
			ja.add(cv);
		}
		return ja;
	}

	private Collection<AssignedLabel<String>> createAssigns(int o, int w, ArrayList<String> categories){
		Collection<AssignedLabel<String>> ret = new ArrayList<AssignedLabel<String>>();
		ArrayList<LObject<String>> lObjects = new ArrayList<LObject<String>>();
		for (int i = 0; i < o; i++) {
			LObject<String> lObject = new LObject<String>("object" + i);
			lObjects.add(lObject);
		}
		for (int i = 0; i < w; i++) {
			Worker<String> worker = new Worker<String>("worker" + i);
			for (LObject<String> lObject : lObjects) {
				AssignedLabel<String> assign = new AssignedLabel<String>(worker, lObject, categories.get(random.nextInt(categories.size())));
				ret.add(assign);
			}
		}
		return ret;
	}

	private NominalProject createProject(String alg, ArrayList<String> categories, boolean priors){
		JobFactory jf = new JobFactory(new GSONSerializer(), new MemoryJobStorage());
		JsonObject jo = new JsonObject();
		jo.addProperty("algorithm", alg);
		jo.add("categories", createCategoriesJsonArray(categories));
		if (priors)
			jo.add("categoryPriors", createCategoryPriorsJsonArray(categories));

		Job job = jf.createNominalJob(jo, "test");
		return (NominalProject)job.getProject();
	}

	@Before
	public void setUp(){
		categories = new ArrayList<String>();
		categories.add("category1");
		categories.add("category2");
	}

	@Test
	public void notFixedPriorsTest() {
		NominalProject project = createProject("IDS", categories, false);
		for (AssignedLabel<String> al : createAssigns(3, 2, categories))
			project.getData().addAssign(al);
		Assert.assertFalse(project.getData().arePriorsFixed());
		Assert.assertTrue(project.getAlgorithm().getModel() instanceof IncrementalNominalModel);
		Assert.assertEquals(2, project.getAlgorithm().getModel().categoryPriors.size());
		Assert.assertEquals(3, ((IncrementalNominalModel) project.getAlgorithm().getModel()).priorDenominator);
		Assert.assertNull(project.getData().getCategoryPriors());
	}

	@Test
	public void fixedPriorsTest() {
		NominalProject project = createProject("IDS", categories, true);
		for (AssignedLabel<String> al : createAssigns(3, 2, categories))
			project.getData().addAssign(al);
		Assert.assertTrue(project.getData().arePriorsFixed());
		Assert.assertTrue(project.getAlgorithm().getModel() instanceof IncrementalNominalModel);
		Assert.assertEquals(0, project.getAlgorithm().getModel().categoryPriors.size());
		Assert.assertEquals(0, ((IncrementalNominalModel) project.getAlgorithm().getModel()).priorDenominator);
		for (String s : categories)
			Assert.assertEquals(0.5, ((IncrementalDawidSkene)project.getAlgorithm()).prior(s), 1e-6);
	}
}
