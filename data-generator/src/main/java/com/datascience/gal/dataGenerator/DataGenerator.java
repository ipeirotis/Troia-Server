package com.datascience.gal.dataGenerator;


import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.datastoring.memory.InMemoryNominalData;
import com.datascience.gal.BatchDawidSkene;
import com.datascience.utils.CostMatrix;
import org.apache.log4j.Logger;

import java.util.*;

/**
 * This class is used to create test data for Troia client tests.
 *
 * @author piotr.gnys@10clouds.com
 */
public class DataGenerator {

	/**
	 * Generate collection of test objects.
	 *
	 * @see TroiaObjectCollection
	 * @param objectCount
	 *            Numbers of objects to generate
	 * @param categories
	 *            Map that associates class names with probability of object
	 *            belonging to that class ( form 0 to 1 )
	 * @return TestObjectCollection object.
	 */
	public TroiaObjectCollection generateTestObjects(int objectCount,
			Map<String, Double> categories) {
		Collection<Double> percentages = categories.values();
		double totalPercentage = 0;
		for (Double percentage : percentages) {
			totalPercentage += percentage.doubleValue();
		}
		if (Math.abs(1 - totalPercentage) > 0.0001) {
			throw new ArithmeticException("Percentage values sum up to "
										  + totalPercentage + " instead of 1.");
		} else {
			int[] borders = new int[percentages.size()];
			int index = 0;
			for (Double percentage : percentages) {
				borders[index] = (int) (percentage * objectCount);
				index++;
			}
			Map<String, String> objects = new HashMap<String, String>();
			index = 0;
			int categortySwitchCounter = 0;
			int categoryIndex = 0;
			Collection<String> categoryNames = categories.keySet();
			Iterator<String> categoryNameIterator = categoryNames.iterator();
			String categoryName = categoryNameIterator.next();
			for (index = 0; index < objectCount; index++) {
				if (categortySwitchCounter < borders[categoryIndex]) {
					if (categoryIndex < categoryNames.size()) {
						categortySwitchCounter++;
					}
				} else {
					categortySwitchCounter = 0;
					categoryIndex++;
					categoryName = categoryNameIterator.next();
				}
				String objectName = "Object-" + index;
				objects.put(objectName, categoryName);
			}
			return new TroiaObjectCollection(objects);
		}
	}

	/**
	 * Generates test object collection with auto generated categories that have
	 * equal distribution among the objects.
	 *
	 * @see TroiaObjectCollection
	 * @param objectCount
	 *            Numbers of objects to generate
	 * @param categoryCount
	 *            Number of categories to generate
	 * @return TestObjectCollection object
	 */
	public TroiaObjectCollection generateTestObjects(int objectCount,
			int categoryCount) {
		Collection<String> categoryNames = this
										   .generateCategoryNames(categoryCount);
		return this.generateTestObjects(objectCount, categoryNames);
	}

	public TroiaObjectCollection generateTestObjects(int objectCount,
			Collection<String> categoryNames) {
		double p = 1.0 / (double) categoryNames.size();
		Double percentage = new Double(p);
		Map<String, Double> categories = new HashMap<String, Double>();
		for (String category : categoryNames) {
			categories.put(category, percentage);
		}
		return this.generateTestObjects(objectCount, categories);
	}

	/**
	 * Generates category names
	 *
	 * @param categoryCount
	 *            Number of categories to generate
	 * @return Collection of category names.
	 */
	public Collection<String> generateCategoryNames(int categoryCount) {
		ArrayList<String> categories = new ArrayList<String>();
		for (int i = 0; i < categoryCount; i++) {
			categories.add("Category-" + i);
		}
		return categories;
	}

	/**
	 * Creates artificial worker that will be working in environment with
	 * categories given as a parameter
	 *
	 * @param name
	 *            Worker name
	 * @param quality
	 *            Quality of the worker (probability that he will label object
	 *            correctly)
	 * @param categories
	 *            Categories that exist in task executed by this worker
	 * @return Artificial worker
	 */
	public ArtificialWorker generateArtificialWorker(String name,
			double quality, Collection<String> categories) {
		ArtificialWorker worker = new ArtificialWorker();
		Map<String, Map<String, Double>> confMatrix = new HashMap<String, Map<String, Double>>();
		Map<String, Double> confVector;
		double wrongProb = 1 - quality;
		Object []array = categories.toArray();
		int length = array.length;
		Random random = new Random();
		for (String correctCategory : categories) {
			double restProb = wrongProb;
			confVector = new HashMap<String, Double>();
			for (String category : categories) {
				confVector.put(category, 0.0);
			}
			confVector.put(correctCategory, quality);
			while (restProb > 0) {
				double prob = Math.random() * (wrongProb / 2.0);
				String category = (String) array[random.nextInt(length)];
				if (!category.equals(correctCategory)) {
					Double actualProb = confVector.get(category);
					if (actualProb == null) {
						actualProb = 0.0;
					}
					actualProb += Math.min(restProb, prob);
					confVector.put(category, actualProb);
					restProb -= prob;
				}

			}
			confMatrix.put(correctCategory, confVector);
		}
		worker.setName(name);
		worker.setConfusionMatrix(new ConfusionMatrix(confMatrix));
		logger.debug("Generated artifical worker with quality " + quality);
		return worker;
	}

	/**
	 * Creates artificial worker, with random work quality, that will be working
	 * in environment with categories given as a parameter
	 *
	 * @param name
	 *            Worker name
	 * @param categories
	 *            Categories that exist in task executed by this worker
	 * @return Artificial worker
	 */
	public ArtificialWorker generateArtificialWorker(String name,
			Collection<String> categories) {
		return this.generateArtificialWorker(name, Math.random(), categories);
	}

	/**
	 * Creates collection of workers, with qualities form given range, that will
	 * operate in environment that contains categories given as a parameter.
	 *
	 * @param workerCount
	 *            Number of workers that will be generated
	 * @param categories
	 *            Collection of categories that workers will be assigning
	 * @param minQuality
	 *            Minimal quality of generated worker (from 0 to 1)
	 * @param maxQuality
	 *            Maximal quality of generated worker (from 0 to 1)
	 * @return Collection of artifical workers
	 */
	public Collection<ArtificialWorker> generateArtificialWorkers(
		int workerCount, Collection<String> categories, double minQuality,
		double maxQuality) {
		Collection<ArtificialWorker> workers = new ArrayList<ArtificialWorker>();
		if (minQuality > maxQuality)
			minQuality = 0;
		double qualityRange = maxQuality - minQuality;
		for (int i = 0; i < workerCount; i++) {
			double quality = Math.random() * qualityRange + minQuality;
			ArtificialWorker worker = this.generateArtificialWorker("Worker-"
									  + i, quality, categories);
			workers.add(worker);
		}
		return workers;
	}

	/**
	 * Generates labels assigned by artificial workers.
	 *
	 * @param workers
	 *            Collection of artificial workers
	 * @param objects
	 *            Test objects collection
	 * @param workersPerObject
	 *            How many workers will assign label to same object
	 * @return Collection of worker assigned labels
	 */
	public Collection<AssignedLabel<String>> generateLabels(Collection<ArtificialWorker> workers, TroiaObjectCollection objects, int workersPerObject) {
		Collection<AssignedLabel<String>> labels = new ArrayList<AssignedLabel<String>>();
		Map<ArtificialWorker, NoisedLabelGenerator> generators = NoisedLabelGeneratorFactory
				.getInstance().getRouletteGeneratorsForWorkers(workers);
		Iterator<ArtificialWorker> workersIterator = workers.iterator();
		for (String object : objects) {
			String correctCat = objects.getCategory(object);
			ArtificialWorker worker;
			for (int labelsForObject = 0; labelsForObject < workersPerObject; labelsForObject++) {
				String assignedLabel;
				if (!workersIterator.hasNext()) {
					workersIterator = workers.iterator();
				}
				worker = workersIterator.next();
				assignedLabel = generators.get(worker).getCategoryWithNoise(
									correctCat);
				labels.add(new AssignedLabel<String>(worker, new LObject<String>(object), assignedLabel));
			}
		}
		return labels;
	}

	/**
	 * Generates gold labels from collection of test objects
	 *
	 * @param objects
	 *            Test objects
	 * @param goldCoverage
	 *            Fraction of objects that will have gold label
	 * @return Collection of gold labels.
	 */
	public Collection<LObject<String>> generateGoldLabels(
		TroiaObjectCollection objects, double goldCoverage) {
		int goldCount = (int) (objects.size() * goldCoverage);
		Collection<LObject<String>> goldLabels = new ArrayList<LObject<String>>();
		Iterator<String> objectsIterator = objects.iterator();
		for (int i = 0; i < goldCount; i++) {
			String objectName;
			if (objectsIterator.hasNext()) {
				objectName = objectsIterator.next();
				LObject<String> goldObject = new LObject<String>(objectName);
				goldObject.setGoldLabel(objects.getCategory(objectName));
				goldLabels.add(goldObject);
			} else {
				break;
			}
		}
		return goldLabels;
	}

	public Collection<Map<String, Object>> computeArtificialWorkerQualities(
			Collection<String> categories,
			TroiaObjectCollection objects,
			Collection<ArtificialWorker> workers,
			Collection<AssignedLabel<String>> labels,
			Collection<LObject<String>> goldLabels) {
		// Objects conversion.
		Collection<AssignedLabel<String>> tsLabels =
				new ArrayList<AssignedLabel<String>>();
		for (AssignedLabel<String> label : labels) {
			tsLabels.add(new AssignedLabel<String>(label.getWorker(), label.getLobject(), label.getLabel()));
		}
		Collection<LObject<String>> tsCorrectLabels =
				new ArrayList<LObject<String>>();
		for (String objectName : objects.testObject.keySet()) {
			LObject<String> goldObject = new LObject<String>(objectName);
			goldObject.setGoldLabel(objects.getCategory(objectName));
			tsCorrectLabels.add(goldObject);
		}

//		com.datascience.gal.DawidSkene dawidSkene =
//				new com.datascience.gal.BatchDawidSkene("data-generation", tsCategories);
//		dawidSkene.addAssignedLabels(tsLabels);
//		dawidSkene.addCorrectLabels(tsCorrectLabels);

		BatchDawidSkene dawidSkene = new BatchDawidSkene();
		InMemoryNominalData data = new InMemoryNominalData();
		for (AssignedLabel<String> assign : tsLabels) {
			data.addAssign(assign);
		}
		dawidSkene.setData(data);

		return null;
	}

	public Data generateTestData(String requestId, int objectCount,
			int categoryCount, int workerCount, double minQuality,
			double maxQuality, double goldRatio, int workersPerObject) {
		Data data = new Data();
		Collection<String> categories = this.generateCategoryNames(
				categoryCount);
		TroiaObjectCollection objects = this.generateTestObjects(objectCount,
				categories);
		CostMatrix<String> cm = CategoryFactory.getInstance().createMatrix(categories);
		Collection<LObject<String>> goldLabels = this.generateGoldLabels(objects,
				goldRatio);
		Collection<ArtificialWorker> workers = null;
		Collection<AssignedLabel<String>> labels = null;
		Collection<String> workerNames = null;
		if(workerCount>0) {
			workers = this.generateArtificialWorkers(workerCount, categories, minQuality, maxQuality);
			labels = this.generateLabels(workers, objects, workersPerObject);
			workerNames = new ArrayList<String>();
			for (ArtificialWorker worker : workers) {
				workerNames.add(worker.getName());
			}
		}
		data.setCategories(categories);
		data.setGoldLabels(goldLabels);
		data.setLabels(labels);
		data.setCostMatrix(cm);
		data.setObjectCollection(objects);
		data.setRequestId(requestId);
		data.setWorkers(workerNames);
		data.setArtificialWorkers(workers);
		data.setArtificialWorkerQualities(computeArtificialWorkerQualities(categories, objects, workers, labels, goldLabels));
		return data;
	}

	public static DataGenerator getInstance() {
		return instance;
	}

	private static DataGenerator instance = new DataGenerator();

	private DataGenerator() {

	}
	public final static double CONFUSION_VECTOR_SUM_EPSILON = 1E-6;

	/**
	 * Logger for this class
	 */
	private static Logger logger = Logger.getLogger(DataGenerator.class);
}
