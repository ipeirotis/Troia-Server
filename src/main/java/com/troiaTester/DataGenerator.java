package main.com.troiaTester;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.log4j.Logger;

import troiaClient.Category;
import troiaClient.CategoryFactory;
import troiaClient.GoldLabel;
import troiaClient.Label;
import troiaClient.MisclassificationCost;
import troiaClient.MisclassificationCostFactory;

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
        if (1 - totalPercentage > 0.0001) {
            throw new ArithmeticException("Percentage values sum up to "
                                          + totalPercentage + " instead of 1.");
        } else {
            int[] borders = new int[percentages.size()];
            int index = 0;
            for (Double percentage : percentages) {
                borders[index] = (int) (percentage.doubleValue() * objectCount);
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
        double p = 1.0 / (double) categoryNames.size();
        Double percentage = new Double(p);
        Map<String, Double> categories = new HashMap<String, Double>();
        for (String category : categoryNames) {
            categories.put(category, percentage);
        }
        return this.generateTestObjects(objectCount, categories);
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
        for (String correctClass : categories) {
            double restProb = wrongProb;
            double prob;
            confVector = new HashMap<String, Double>();
            for (String labeledClass : categories) {
                if (labeledClass.equalsIgnoreCase(correctClass)) {
                    confVector.put(labeledClass, quality);
                    logger.debug("Set correct label probability to " + quality);
                } else {
                    prob = Math.random() * restProb;
                    restProb -= prob;
                    confVector.put(labeledClass, prob);
                }
            }
            confMatrix.put(correctClass, confVector);
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
    public Collection<Label> generateLabels(
        Collection<ArtificialWorker> workers, TroiaObjectCollection objects,
        int workersPerObject) {
        Collection<Label> labels = new ArrayList<Label>();
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
                labels.add(new Label(worker.getName(), object, assignedLabel));
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
    public Collection<GoldLabel> generateGoldLabels(
        TroiaObjectCollection objects, double goldCoverage) {
        int goldCount = (int) (objects.size() * goldCoverage);
        Collection<GoldLabel> goldLabels = new ArrayList<GoldLabel>();
        Iterator<String> objectsIterator = objects.iterator();
        for (int i = 0; i < goldCount; i++) {
            String objectName;
            if (objectsIterator.hasNext()) {
                objectName = objectsIterator.next();
                goldLabels.add(new GoldLabel(objectName, objects
                                             .getCategory(objectName)));
            } else {
                break;
            }
        }
        return goldLabels;
    }

    public Data generateTestData(String requestId, int objectCount,
                                 int categoryCount, int workerCount, double minQuality,
                                 double maxQuality, double goldRatio, int workersPerObject) {
        Data data = new Data();
        Collection<String> categoryNames = this
                                           .generateCategoryNames(categoryCount);
        Collection<Category> categories = CategoryFactory.getInstance()
                                          .createCategories(categoryNames);
        TroiaObjectCollection objects = this.generateTestObjects(objectCount,
                                        categoryNames);
        Collection<MisclassificationCost> misclassificationCost = MisclassificationCostFactory
                .getInstance().getMisclassificationCosts(categories);
        Collection<GoldLabel> goldLabels = this.generateGoldLabels(objects,
                                           goldRatio);
        Collection<ArtificialWorker> workers = null;
        Collection<Label> labels = null;
        Collection<String> workerNames = null;
        if(workerCount>0) {
            workers = this.generateArtificialWorkers(workerCount, categoryNames, minQuality, maxQuality);
            labels = this.generateLabels(workers, objects,workersPerObject);
            workerNames = new ArrayList<String>();
            for (ArtificialWorker worker : workers) {
                workerNames.add(worker.getName());
            }
        }
        data.setCategories(categories);
        data.setGoldLabels(goldLabels);
        data.setLabels(labels);
        data.setMisclassificationCost(misclassificationCost);
        data.setObjectCollection(objects);
        data.setRequestId(requestId);
        data.setWorkers(workerNames);
        data.setArtificialWorkers(workers);
        return data;
    }

    public static DataGenerator getInstance() {
        return instance;
    }

    private static DataGenerator instance = new DataGenerator();

    private DataGenerator() {

    }

    /**
     * Logger for this class
     */
    private static Logger logger = Logger.getLogger(DataGenerator.class);
}
