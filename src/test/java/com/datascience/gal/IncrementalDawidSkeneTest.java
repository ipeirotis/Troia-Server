/**
 *
 */
package com.datascience.gal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.datascience.gal.IncrementalDawidSkene.IncrementalDawidSkeneDeserializer;
import com.datascience.gal.service.JSONUtils;
import com.datascience.utils.auxl.TestDataManager;
import com.google.gson.JsonObject;

/**
 * @author Michael Arshynov
 *
 */
public class IncrementalDawidSkeneTest {
	int testCount = 5;
	List<String> idList = new ArrayList<String>();
	List<ArrayList<Category>> categoriesList = new ArrayList<ArrayList<Category>>();
	List<HashMap<String, Category>> categoryMapList;
	List<HashMap<String, Datum>> objectsList;
	List<HashMap<String, Worker>> workersList;
	/**
	 *
	 */
	public IncrementalDawidSkeneTest() {
		idList.add("id1");
		idList.add("id2");
		idList.add("id3");
		idList.add("id4");
		idList.add("id5");
		String[] categoryNames = {"cat1", "cat2", "cat3", "cat4", "cat5"};
		objectsList = new ArrayList<HashMap<String,Datum>>();
		workersList = new ArrayList<HashMap<String,Worker>>();
		categoryMapList = new ArrayList<HashMap<String,Category>>();

		for (int i=0; i<testCount; i++) {
			ArrayList<Category> categoryList = new ArrayList<Category>();
			HashMap<String, Datum> objectMap = new HashMap<String, Datum>();
			HashMap<String, Worker> workerMap = new HashMap<String, Worker>();
			HashMap<String,Category> categoryMap = new HashMap<String, Category>();
			for (int j=i; j<testCount; j++) {
				Category category = new Category(categoryNames[j]);
				categoryList.add(category);

				Datum datum = new Datum("datum"+i+"."+j, new HashSet(categoryList));
				objectMap.put("datum"+i+"."+j, datum);

				Worker worker = new Worker("worker"+i+"."+j, new HashSet(categoryList));
				workerMap.put("worker"+i+"."+j, worker);

				categoryMap.put("categoryMap"+i+"."+j, category);
			}
			categoryMapList.add(categoryMap);
			categoriesList.add(categoryList);
			objectsList.add(objectMap);
			workersList.add(workerMap);
		}
	}
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
	 * Test method for {@link com.datascience.gal.IncrementalDawidSkene#initializePriors()}.
	 * Test method for {@link com.datascience.gal.IncrementalDawidSkene#prior(java.lang.String)}.
	 */
	@Test
	public final void testInitializePriors() {
		for (int i=0; i<testCount; i++) {
			List<Category> localCategoryList = new ArrayList<Category>();

			for (Category category: categoriesList.get(i)) {
				Category localCategory = new Category(category.getName());
				localCategory.setPrior(0.1);
				localCategoryList.add(localCategory);
			}

			IncrementalDawidSkene incrementalDawidSkene =
				new IncrementalDawidSkene(idList.get(i), localCategoryList);

			incrementalDawidSkene.initializePriors();
			double expected = 1. / localCategoryList.size();

			double actual = incrementalDawidSkene.prior(localCategoryList.get(0).getName());
			assertEquals(expected, actual, TestDataManager.DELTA_DOUBLE);
		}
	}

	/**
	 * Test method for {@link com.datascience.gal.IncrementalDawidSkene#getErrorRateForWorker(com.datascience.gal.Worker, java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testGetErrorRateForWorker() {
		for (int i=0; i<testCount; i++) {
			List<Category> localCategoryList = new ArrayList<Category>();
			List<Category> globalCategoryList = categoriesList.get(i);
			int size = globalCategoryList.size();

			for (Category category: globalCategoryList) {
				Category localCategory = new Category(category.getName());
				localCategory.setPrior(0.4);
				localCategoryList.add(localCategory);
			}
			Set<Category> localCategorySet = new HashSet<Category>(localCategoryList);
			Worker worker = new Worker("worker", localCategorySet);
			IncrementalDawidSkene incrementalDawidSkene = new
			IncrementalDawidSkene("id"+i, localCategoryList);
			for (int j=0; j<size; j++) {
				int k = ( j + 1 ) % size;
				double errorRate = incrementalDawidSkene.getErrorRateForWorker(worker,
								   globalCategoryList.get(j).getName(), globalCategoryList.get(k).getName());
				assertTrue(errorRate>=0 && errorRate<=1);
			}
		}

	}

	/**
	 * Test method for {@link com.datascience.gal.IncrementalDawidSkene#getObjectClassProbabilities(java.lang.String, java.lang.String)}.
	 *
	 */
	@Test
	public final void testGetObjectClassProbabilitiesStringString() {
		for (int i=0; i<testCount; i++) {
			List<Category> localCategoryList = new ArrayList<Category>();
			List<Category> globalCategoryList = categoriesList.get(i);

			for (Category category: globalCategoryList) {
				Category localCategory = new Category(category.getName());
				localCategory.setPrior(0.2);
				localCategoryList.add(localCategory);
			}

			IncrementalDawidSkene incrementalDawidSkene1  = constructByDeserialization("id"+i,
					objectsList.get(i), workersList.get(i), categoryMapList.get(i),
					true, IncrementalDSMethod.ITERATELOCAL, 0.3);
			IncrementalDawidSkene incrementalDawidSkene2  = constructByDeserialization("id"+i,
					objectsList.get(i), workersList.get(i), categoryMapList.get(i),
					false, IncrementalDSMethod.ITERATELOCAL, 0.3);
			IncrementalDawidSkene incrementalDawidSkene3  = constructByDeserialization("id"+i,
					objectsList.get(i), workersList.get(i), categoryMapList.get(i),
					true, IncrementalDSMethod.UPDATEWORKERS, 0.3);
			IncrementalDawidSkene incrementalDawidSkene4  = constructByDeserialization("id"+i,
					objectsList.get(i), workersList.get(i), categoryMapList.get(i),
					false, IncrementalDSMethod.UPDATEWORKERS, 0.3);

			Datum firstDatumInMap = (Datum) objectsList.get(i).values().toArray()[0];
			String objectName = firstDatumInMap.getName();

			Map<String, Double> ret = incrementalDawidSkene1.getObjectClassProbabilities(objectName, null);
			Map<String, Double> catProbability = firstDatumInMap.getCategoryProbability();
			assertEquals(catProbability.size(), ret.size());
			assertTrue(compareHashMaps(catProbability, ret));

			ret = incrementalDawidSkene2.getObjectClassProbabilities(objectName, null);
			catProbability = firstDatumInMap.getCategoryProbability();
			assertEquals(catProbability.size(), ret.size());
			assertTrue(compareHashMaps(catProbability, ret));

			ret = incrementalDawidSkene3.getObjectClassProbabilities(objectName, null);
			catProbability = firstDatumInMap.getCategoryProbability();
			assertEquals(catProbability.size(), ret.size());
			assertTrue(compareHashMaps(catProbability, ret));

			ret = incrementalDawidSkene4.getObjectClassProbabilities(objectName, null);
			catProbability = firstDatumInMap.getCategoryProbability();
			assertEquals(catProbability.size(), ret.size());
			assertTrue(compareHashMaps(catProbability, ret));
		}
	}
	/**
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean compareHashSets(HashSet s1, HashSet s2) {
		if(s1== null && s2==null)
			return true;
		if(s1 != null && s2 != null)
			return s1.containsAll(s2) && s2.containsAll(s1);
		else
			return false;
	}
	/**
	 * @param m1
	 * @param m2
	 * @return
	 */
	private <T1, T2> boolean compareHashMaps(Map<T1, T2> m1, Map<T1, T2> m2) {
		if(m1== null && m2==null)
			return true;
		if(m1 != null && m2 != null) {
			for (Entry<T1, T2> entry : m1.entrySet()) {
				T1 key = entry.getKey();
				if  (!entry.getValue().equals(m2.get(key)))
					return false;
			}
			return true;
		} else return false;
	}
	/**
	 * @param id
	 * @param objects
	 * @param workers
	 * @param categories
	 * @param fixedPriors
	 * @param priorDenominator
	 * @return
	 */
	private IncrementalDawidSkene constructByDeserialization(String id, Map<String, Datum> objects,
			Map<String, Worker> workers, Map<String, Category> categories,
			boolean fixedPriors, IncrementalDSMethod dsmethod, double priorDenominator) {
		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("id", id);
		jsonObject.add("objects", JSONUtils.gson.toJsonTree(objects, JSONUtils.stringDatumMapType));
		jsonObject.add("workers", JSONUtils.gson.toJsonTree(workers, JSONUtils.strinWorkerMapType));
		jsonObject.add("categories", JSONUtils.gson.toJsonTree(categories, JSONUtils.stringCategoryMapType));
		jsonObject.addProperty("fixedPriors", fixedPriors);
		jsonObject.addProperty("dsmethod", IncrementalDSMethod.ITERATELOCAL.toString());
		jsonObject.addProperty("priorDenominator", priorDenominator);
		IncrementalDawidSkene.IncrementalDawidSkeneDeserializer deserializer = new IncrementalDawidSkeneDeserializer();
		IncrementalDawidSkene incrementalDawidSkene = deserializer.deserialize(jsonObject, null, null);
		return incrementalDawidSkene;
	}


	/**
	 * Test method for {@link com.datascience.gal.IncrementalDawidSkene#computePriors()}.
	 */
	@Test
	public final void testComputePriors() {
		for (int i=0; i<testCount; i++) {
			List<Category> globalCategoryList = categoriesList.get(i);

			double priorDenominator = 0.15;
			double priorByDefault = 0.8;

			IncrementalDawidSkene incrementalDawidSkene =
				new IncrementalDawidSkene("id"+i, globalCategoryList);
			Map<String, Double> computedPriors = incrementalDawidSkene.computePriors();
			for (Entry<String, Double> entry :
					computedPriors.entrySet()) {
				double actualValue =  (Double) entry.getValue();
				assertEquals( 1./globalCategoryList.size(), actualValue,  TestDataManager.DELTA_DOUBLE);
			}

			IncrementalDawidSkene incrementalDawidSkeneFullConstructionFixedPriors =
				constructByDeserialization("idfcFixed"+i, objectsList.get(i), workersList.get(i),
										   categoryMapList.get(i), true, IncrementalDSMethod.ITERATELOCAL, 0.1);
			computedPriors = incrementalDawidSkeneFullConstructionFixedPriors.computePriors();
			for (Entry<String, Double> entry :
					computedPriors.entrySet()) {
				double actualValue =  (Double) entry.getValue();
				assertEquals(1./globalCategoryList.size(), actualValue, TestDataManager.DELTA_DOUBLE);
			}

			IncrementalDawidSkene incrementalDawidSkeneFullConstructionNotFixedPriorsWithZeroValues =
				constructByDeserialization("idfcNotFixed"+i, objectsList.get(i), workersList.get(i),
										   categoryMapList.get(i), false, IncrementalDSMethod.ITERATELOCAL, priorDenominator);
			computedPriors = incrementalDawidSkeneFullConstructionNotFixedPriorsWithZeroValues.computePriors();
			for (Entry<String, Double> entry :
					computedPriors.entrySet()) {
				double actualValue =  (Double) entry.getValue();
				assertEquals(0.0, actualValue, TestDataManager.DELTA_DOUBLE);
			}
			List<Category> localCategoryList = new ArrayList<Category>();
			for (Category category: globalCategoryList) {
				Category localCategory = new Category(category.getName());
				localCategory.setPrior(0.2);
				localCategoryList.add(localCategory);
			}


			HashMap<String, Category> categoryMapGlobal = categoryMapList.get(i);
			HashMap<String, Category> categoryMapLocal = new HashMap<String, Category>();
			for (Entry<String, Category> entry : categoryMapGlobal.entrySet()) {
				Category category = new Category(((Category)entry.getValue()).getName());
				category.setPrior(priorByDefault);
				categoryMapLocal.put(entry.getKey().toString(), category);
			}

			IncrementalDawidSkene incrementalDawidSkeneFullConstructionNotFixedPriors =
				constructByDeserialization("idfcNotFixed"+i, objectsList.get(i), workersList.get(i),
										   categoryMapLocal, false, IncrementalDSMethod.ITERATELOCAL, priorDenominator);
			computedPriors = incrementalDawidSkeneFullConstructionNotFixedPriors.computePriors();
			for (Entry<String, Double> entry :
					computedPriors.entrySet()) {
				double actualValue =  (Double) entry.getValue();
				assertEquals(priorByDefault/priorDenominator, actualValue, TestDataManager.DELTA_DOUBLE);
			}
		}
	}

	/**
	 * Test method for {@link com.datascience.gal.IncrementalDawidSkene#prior(java.lang.String)}.
	 */
	@Test
	public final void testPrior() {
		Map<String, Datum> objects = new HashMap<String, Datum>();
		Map<String, Worker> workers = new HashMap<String, Worker>();
		Map<String, Category> categories = new HashMap<String, Category>();

		Category category1 = new Category("category1");
		category1.setPrior(0.1);
		Category category2 = new Category("category2");
		category2.setPrior(0.2);
		categories.put(category1.getName(), category1);
		categories.put(category2.getName(), category2);
		Set<Category> categorySetForWorkerAndDatum = new HashSet<Category>(2);
		categorySetForWorkerAndDatum.add(category1);
		categorySetForWorkerAndDatum.add(category2);

		Worker worker1 = new Worker("worker1", categorySetForWorkerAndDatum);
		Worker worker2 = new Worker("worker2", categorySetForWorkerAndDatum);
		workers.put(worker1.getName(), worker1);
		workers.put(worker2.getName(), worker2);

		Datum datum1 = new Datum("datum1", categorySetForWorkerAndDatum);
		datum1.setGold(true);
		Datum datum2 = new Datum("datum2", categorySetForWorkerAndDatum);
		datum2.setGold(true);
		objects.put(datum1.getName(), datum1);
		objects.put(datum2.getName(), datum2);

		IncrementalDawidSkene incrementalDawidSkeneForEmptyDatumMap =
			constructByDeserialization("id1", objects, workers, categories, true,
									   IncrementalDSMethod.ITERATELOCAL, 0.7);
		assertEquals(0.5, incrementalDawidSkeneForEmptyDatumMap.prior("category2"), TestDataManager.DELTA_DOUBLE);
		assertEquals(0.5, incrementalDawidSkeneForEmptyDatumMap.prior("category1"), TestDataManager.DELTA_DOUBLE);

		incrementalDawidSkeneForEmptyDatumMap =
			constructByDeserialization("id1", objects, workers, categories, false,
									   IncrementalDSMethod.ITERATELOCAL, 0.7);
		assertEquals(0.28571, incrementalDawidSkeneForEmptyDatumMap.prior("category2"), TestDataManager.DELTA_DOUBLE);
		assertEquals(0.14285, incrementalDawidSkeneForEmptyDatumMap.prior("category1"), TestDataManager.DELTA_DOUBLE);


	}

	/**
	 * Test method for {@link com.datascience.gal.IncrementalDawidSkene#addCorrectLabel(com.datascience.gal.CorrectLabel)}.
	 */
	@Test
	public final void testAddCorrectLabel() {
		Map<String, Datum> objects = new HashMap<String, Datum>();
		Map<String, Worker> workers = new HashMap<String, Worker>();
		Map<String, Category> categories = new HashMap<String, Category>();

		Category category1 = new Category("category1");
		category1.setPrior(0.1);
		Category category2 = new Category("category2");
		category2.setPrior(0.2);
		categories.put(category1.getName(), category1);
		categories.put(category2.getName(), category2);
		Set<Category> categorySetForWorkerAndDatum = new HashSet<Category>(2);
		categorySetForWorkerAndDatum.add(category1);
		categorySetForWorkerAndDatum.add(category2);

		Worker worker1 = new Worker("worker1", categorySetForWorkerAndDatum);
		Worker worker2 = new Worker("worker2", categorySetForWorkerAndDatum);
		workers.put(worker1.getName(), worker1);
		workers.put(worker2.getName(), worker2);

		Datum datum1 = new Datum("datum1", categorySetForWorkerAndDatum);

		Datum datum2 = new Datum("datum2", categorySetForWorkerAndDatum);

		datum1.setGold(true);
		datum2.setGold(true);

		datum1.setCorrectCategory("category1");
		datum2.setCorrectCategory("category2");
		objects.put(datum1.getName(), datum1);
		objects.put(datum2.getName(), datum2);


		IncrementalDawidSkene incrementalDawidSkeneIterateLocal1 =
			constructByDeserialization("id1", objects,
									   workers, categories, true,
									   IncrementalDSMethod.ITERATELOCAL, 0.7);

		IncrementalDawidSkene incrementalDawidSkeneIterateLocal2 =
			constructByDeserialization("id2", objects,
									   workers, categories, true,
									   IncrementalDSMethod.ITERATELOCAL, 0.7);

		IncrementalDawidSkene incrementalDawidSkenepdateWorkers1 =
			constructByDeserialization("id3", objects,
									   workers, categories, true,
									   IncrementalDSMethod.UPDATEWORKERS, 0.7);

		IncrementalDawidSkene incrementalDawidSkenepdateWorkers2 =
			constructByDeserialization("id4", objects,
									   workers, categories, true,
									   IncrementalDSMethod.UPDATEWORKERS, 0.7);

		doTestAddCorrectLabel(incrementalDawidSkeneIterateLocal1, "datum1", null, true);
		doTestAddCorrectLabel(incrementalDawidSkeneIterateLocal1, "datum2", null, true);
		doTestAddCorrectLabel(incrementalDawidSkeneIterateLocal2, "datum1", null, true);
		doTestAddCorrectLabel(incrementalDawidSkeneIterateLocal2, "datum2", null, true);
		doTestAddCorrectLabel(incrementalDawidSkenepdateWorkers1, "datum1", null, true);
		doTestAddCorrectLabel(incrementalDawidSkenepdateWorkers1, "datum2", null, true);
		doTestAddCorrectLabel(incrementalDawidSkenepdateWorkers2, "datum1", null, true);
		doTestAddCorrectLabel(incrementalDawidSkenepdateWorkers2, "datum2", null, true);


		CorrectLabel correctLabel1 = new CorrectLabel("datum1", "category1");
		CorrectLabel correctLabel2 = new CorrectLabel("datum2", "category2");
		incrementalDawidSkeneIterateLocal1.addCorrectLabel(correctLabel1);
		incrementalDawidSkenepdateWorkers1.addCorrectLabel(correctLabel1);
		incrementalDawidSkeneIterateLocal2.addCorrectLabel(correctLabel2);
		incrementalDawidSkenepdateWorkers2.addCorrectLabel(correctLabel2);


		doTestAddCorrectLabel(incrementalDawidSkeneIterateLocal1, "datum1", "category1", false);
		doTestAddCorrectLabel(incrementalDawidSkeneIterateLocal2, "datum2", "category2", false);
		doTestAddCorrectLabel(incrementalDawidSkenepdateWorkers1, "datum1", "category1", false);
		doTestAddCorrectLabel(incrementalDawidSkenepdateWorkers2, "datum2", "category2", false);

		System.err.println("d1 "+incrementalDawidSkeneIterateLocal1.getObjectClassProbabilities("datum1", null));
		System.err.println("d2 "+incrementalDawidSkeneIterateLocal1.getObjectClassProbabilities("datum2", null));

	}

	/**
	 * @param incrementalDawidSkene
	 * @param objectName
	 * @param correctCategory
	 * @param isStateBeforeCorrectLabelAdded
	 */
	public final void doTestAddCorrectLabel(IncrementalDawidSkene incrementalDawidSkene,
											String objectName, String correctCategory, boolean isStateBeforeCorrectLabelAdded) {
		Map<String, Double> probabilitiesMap = incrementalDawidSkene.getObjectClassProbabilities(objectName, null);
		assertEquals(2, probabilitiesMap.size());
		if (isStateBeforeCorrectLabelAdded) {
			for (Entry<String, Double> entry: probabilitiesMap.entrySet()) {
				assertEquals(0.5, entry.getValue().doubleValue(), TestDataManager.DELTA_DOUBLE);
			}
		} else {
			for (Entry<String, Double> entry: probabilitiesMap.entrySet()) {
				String categoryName = entry.getKey();
				double expected = categoryName.equals(correctCategory) ? 1.0 : 0.0;
				System.err.println();
				assertEquals(expected, entry.getValue().doubleValue(), TestDataManager.DELTA_DOUBLE);
			}
		}
	}
	/**
	 * Test method for {@link com.datascience.gal.IncrementalDawidSkene#IncrementalDawidSkene(java.lang.String, java.util.Collection, com.datascience.gal.IncrementalDSMethod)}.
	 */
	@Test
	public final void testIncrementalDawidSkeneStringCollectionOfCategoryIncrementalDSMethod() {
		for (int i=0; i<testCount; i++) {
			IncrementalDawidSkene incrementalDawidSkeneIterateLocal = new IncrementalDawidSkene(
				idList.get(i), categoriesList.get(i), IncrementalDSMethod.ITERATELOCAL);
			IncrementalDawidSkene incrementalDawidSkeneUpdateWorkers = new IncrementalDawidSkene(
				idList.get(i), categoriesList.get(i), IncrementalDSMethod.UPDATEWORKERS);
			assertNotSame(incrementalDawidSkeneIterateLocal.hashCode(),
						  incrementalDawidSkeneUpdateWorkers.hashCode());
		}
	}
	/**
	 * Test method for {@link com.datascience.gal.IncrementalDawidSkene#estimate(int)}.
	 */
	@Test
	public final void testEstimateGoldDatumAndWithoutObjectListAtAll() {
		for (int i=0; i<testCount; i++) {
			List<Category> localCategoryList = new ArrayList<Category>();
			HashMap<String, Datum> objectMapEmpty = new HashMap<String, Datum>();;
			HashMap<String, Datum> objectMapNotEmpty = new HashMap<String, Datum>();;

			for (Category category: categoriesList.get(i)) {
				Category localCategory = new Category(category.getName());
				localCategory.setPrior(0.1);
				localCategoryList.add(localCategory);
			}

			HashSet<Category> categorySetForDatum = new HashSet<Category>();
			for (Entry<String, Category> entry :categoryMapList.get(i).entrySet()) {
				Category cat = (Category) entry.getValue();
				cat.setName(entry.getKey());
				categorySetForDatum.add(cat);
			}

			Datum datum = new Datum("datum"+i, categorySetForDatum);
			datum.setGold(true);
			for (Category category: categorySetForDatum) {
				datum.setCategoryProbability(category.getName(), 0.1);
			}

			for (Entry<String, Category> entry : categoryMapList.get(i).entrySet()) {
				entry.getValue();
				objectMapNotEmpty.put("datum"+i, datum);
			}

			IncrementalDawidSkene incrementalDawidSkeneForNotEmptyDatumMap =
				constructByDeserialization(idList.get(i), objectMapNotEmpty,
										   workersList.get(i), categoryMapList.get(i), true,
										   IncrementalDSMethod.ITERATELOCAL, 0.7);

			incrementalDawidSkeneForNotEmptyDatumMap.estimate(10);

			IncrementalDawidSkene incrementalDawidSkeneForEmptyDatumMap =
				constructByDeserialization(idList.get(i), objectMapEmpty,
										   workersList.get(i), categoryMapList.get(i), true,
										   IncrementalDSMethod.ITERATELOCAL, 0.7);
			incrementalDawidSkeneForEmptyDatumMap.estimate(10);
		}
	}
	/**
	 * Test method for {@link com.datascience.gal.IncrementalDawidSkene#estimate(int)}.
	 */
	@Test
	public final void testEstimateGold() {
		doTestEstimateForGold(true);
	}
	/**
	 * Test method for {@link com.datascience.gal.IncrementalDawidSkene#estimate(int)}.
	 * Test method for {@link com.datascience.gal.IncrementalDawidSkene#addAssignedLabel(com.datascience.gal.AssignedLabel)}.
	 */
	@Test
	public final void testEstimateNotGold() {
		doTestEstimateForGold(false);
	}

	/**
	 * @param isGold
	 */
	private final void doTestEstimateForGold(boolean isGold) {
		Map<String, Datum> objects = new HashMap<String, Datum>();
		Map<String, Worker> workers = new HashMap<String, Worker>();
		Map<String, Category> categories = new HashMap<String, Category>();

		Category category1 = new Category("category1");
		category1.setPrior(0.1);
		Category category2 = new Category("category2");
		category2.setPrior(0.2);
		categories.put(category1.getName(), category1);
		categories.put(category2.getName(), category2);
		Set<Category> categorySetForWorkerAndDatum = new HashSet<Category>(2);
		categorySetForWorkerAndDatum.add(category1);
		categorySetForWorkerAndDatum.add(category2);

		Worker worker1 = new Worker("worker1", categorySetForWorkerAndDatum);
		Worker worker2 = new Worker("worker2", categorySetForWorkerAndDatum);
		workers.put(worker1.getName(), worker1);
		workers.put(worker2.getName(), worker2);

		Datum datum1 = new Datum("datum1", categorySetForWorkerAndDatum);

		Datum datum2 = new Datum("datum2", categorySetForWorkerAndDatum);
		if (isGold) {
			datum1.setGold(true);
			datum2.setGold(true);
		} else {
			AssignedLabel assignedLabel1 = new AssignedLabel("worker1", "datum1", "category1");
			datum1.addAssignedLabel(assignedLabel1);

			AssignedLabel assignedLabel2 = new AssignedLabel("worker2", "datum2", "category2");
			datum2.addAssignedLabel(assignedLabel2);
		}
		datum1.setCorrectCategory("category1");
		datum2.setCorrectCategory("category2");
		objects.put(datum1.getName(), datum1);
		objects.put(datum2.getName(), datum2);

		IncrementalDawidSkene incrementalDawidSkene =
			constructByDeserialization("id1", objects, workers, categories, true,
									   IncrementalDSMethod.ITERATELOCAL, 0.7);
		Map<String,Double> objectsRet = incrementalDawidSkene.getObjectClassProbabilities("datum1", null);

		assertEquals(2, objectsRet.size());

		for(Entry<String, Double> entry : objectsRet.entrySet()) {
			assertEquals(0.5, entry.getValue().doubleValue(), TestDataManager.DELTA_DOUBLE);
		}
		incrementalDawidSkene.estimate(5);
		objectsRet = incrementalDawidSkene.getObjectClassProbabilities("datum1", null);
		assertEquals(2, objectsRet.size());
		assertEquals(1.0, objectsRet.get("category1").doubleValue(),TestDataManager.DELTA_DOUBLE);
		assertEquals(0.0, objectsRet.get("category2").doubleValue(),TestDataManager.DELTA_DOUBLE);

		objectsRet = incrementalDawidSkene.getObjectClassProbabilities("datum2", null);
		assertEquals(2, objectsRet.size());
		assertEquals(1.0, objectsRet.get("category2").doubleValue(),TestDataManager.DELTA_DOUBLE);
		assertEquals(0.0, objectsRet.get("category1").doubleValue(),TestDataManager.DELTA_DOUBLE);
	}

	/**
	 * Test method for {@link com.datascience.gal.IncrementalDawidSkene#getWorkerPriors(com.datascience.gal.Worker)}.
	 */
	@Test
	public final void testGetWorkerPriors() {
		for (int i=0; i<testCount; i++) {
			IncrementalDawidSkene incrementalDawidSkene  = constructByDeserialization("id"+i,
					objectsList.get(i), workersList.get(i), categoryMapList.get(i),
					true, IncrementalDSMethod.ITERATELOCAL, 0.3);
			for (Entry<String, Worker> entry : workersList.get(i).entrySet()) {
				Worker worker = entry.getValue();
				HashMap<String, Category> categoryMap = categoryMapList.get(i);
				Map<String, Double> workerPriors = incrementalDawidSkene.getWorkerPriors(worker);
				for (Entry<String, Category> entryCatMap : categoryMap.entrySet()) {
					assertTrue(workerPriors.containsKey(entryCatMap.getKey()));
				}
			}
		}
	}

}
