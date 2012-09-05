package main.com.troiaTester;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import troiaClient.CategoryFactory;

public class TroiaDataGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Logger.getRootLogger().setLevel(Level.ERROR);
		try {
			if(args.length<2){
				printHelp();
			}else{
				parseArgs(args);
				verifyParameters();
				generateData();
				saveData();
				System.out.println("Test data generation finished.");
			}
		} catch (Exception e) {
			System.out.println("Unable to generate test data because "
					+ e.getMessage());
		}
	}

	private static void parseArgs(String[] args) {
	    workersPerObject=1;
	    minQuality=0;
	    maxQuality=1;
	    goldRatio=0;
		for (int argPointer = 0; argPointer < args.length; argPointer++) {
			if (args[argPointer].equalsIgnoreCase(CONFIGURATION_FILE_TAG)) {
				if (argPointer++ < args.length) {
					try {
						parsePropertiesFile(args[argPointer]);
					} catch (IOException e) {
						System.out.println('\"' + args[argPointer]
								+ "\" is not a correct property file.");
						Logger.getLogger(TroiaDataGenerator.class).error(e.getMessage());
					}
				}
			} else if (args[argPointer].equalsIgnoreCase(BASIC_WORKER_FILE_TAG)) {
				if (argPointer++ < args.length) {
					workerQualitiesFilename = args[argPointer];
				}
			} else if (args[argPointer].equalsIgnoreCase(CATEGORIES_FILE_TAG)) {
				if (argPointer++ < args.length) {
					categoriesFileName = args[argPointer];
				}
			} else if (args[argPointer].equalsIgnoreCase(CATEGORY_COUNT_TAG)) {
				if (argPointer++ < args.length) {
					categoryCount = Integer.parseInt(args[argPointer]);
				}
			} else if (args[argPointer].equalsIgnoreCase(OBJECT_COUNT_TAG)) {
				if (argPointer++ < args.length) {
					objectCount = Integer.parseInt(args[argPointer]);
				}
			} else if (args[argPointer].equalsIgnoreCase(WORKER_COUNT_TAG)) {
				if (argPointer++ < args.length) {
					workerCount = Integer.parseInt(args[argPointer]);
				}
			} else if (args[argPointer]
					.equalsIgnoreCase(WORKERS_PER_OBJECT_TAG)) {
				if (argPointer++ < args.length) {
					workersPerObject = Integer.parseInt(args[argPointer]);
				}
			} else if (args[argPointer].equalsIgnoreCase(MIN_QUALITY_TAG)) {
				if (argPointer++ < args.length) {
					minQuality = Double.parseDouble(args[argPointer]);
				}
			} else if (args[argPointer].equalsIgnoreCase(MAX_QUALITY_TAG)) {
				if (argPointer++ < args.length) {
					maxQuality = Double.parseDouble(args[argPointer]);
				}
			} else if (args[argPointer].equalsIgnoreCase(GOLD_RATIO_TAG)) {
				if (argPointer++ < args.length) {
					goldRatio = Double.parseDouble(args[argPointer]);
				}
			}else if (args[argPointer].equalsIgnoreCase(TEST_DATA_FILE_TAG)) {
				if (argPointer++ < args.length) {
					outputFilename = args[argPointer];
				}
			}
		}
	}

	private static void verifyParameters() throws Exception {
		if (categoryCount <= 0 && categoriesFileName == null) {
			throw new Exception("Category count must be larger then 0.");
		}
		if (objectCount <= 0) {
			throw new Exception("Object count must be larger then 0.");
		}
		if (workerQualitiesFilename != null) {
			if (workerCount <= 0) {
				throw new Exception("Worker count must be larger then 0.");
			}
			if (minQuality < 0 || minQuality > 1) {
				throw new Exception(
						"Worker quality must be value between 0 and 1.");
			}
			if (maxQuality < 0 || maxQuality > 1) {
				throw new Exception(
						"Worker quality must be value between 0 and 1.");
			}
			if (minQuality > maxQuality) {
				throw new Exception(
						"Minimum quality must be equal or smaller then maximum quality");
			}
		}
		if (workersPerObject <= 0) {
			throw new Exception(
					"Workers per object must have value larger then 0.");
		}
		if (goldRatio < 0 || goldRatio > 1) {
			throw new Exception("Gold ratio must be value between 0 and 1.");
		}
	}

	private static void generateData() throws FileNotFoundException {
		Collection<String> categoryNames = null;
		if (categoriesFileName != null) {
			categories = manager
					.loadCategoriesWithProbabilities(categoriesFileName);
			data.setCategories(CategoryFactory.getInstance().createCategories(
					categories.keySet()));
			categoryNames=categories.keySet();
		} else {
			categoryNames = generator.generateCategoryNames(categoryCount);
			data.setCategories(CategoryFactory.getInstance().createCategories(
					categoryNames));
		}
		if (workerQualitiesFilename != null) {
			data.setArtificialWorkers(manager.loadBasicWorkers(
					workerQualitiesFilename, categoryNames));

		} else {
			data.setArtificialWorkers(generator.generateArtificialWorkers(
					workerCount, categoryNames, minQuality, maxQuality));
		}
		data.setObjectCollection(generator.generateTestObjects(objectCount, categoryNames));
		data.setGoldLabels(generator.generateGoldLabels(data.getObjectCollection(),goldRatio));
		data.setLabels(generator.generateLabels(data.getArtificialWorkers(),data.getObjectCollection(), workersPerObject));
	}

	private static void saveData() throws IOException {
		manager.saveTestData(outputFilename, data);
	}

	private static void parsePropertiesFile(String filename) throws IOException {
		Properties props = new Properties();
		FileInputStream inputStream = new FileInputStream(filename);
		props.load(inputStream);
		categoryCount = Integer.parseInt(props
				.getProperty(CATEGORY_COUNT_PROPERTY));
		objectCount = Integer
				.parseInt(props.getProperty(OBJECT_COUNT_PROPERTY));
		workerCount = Integer
				.parseInt(props.getProperty(WORKER_COUNT_PROPERTY));
		minQuality = Double.parseDouble(props
				.getProperty(MINIMUM_QUALITY_PROPERTY));
		maxQuality = Double.parseDouble(props
				.getProperty(MAXIMUM_QUALITY_PROPERY));
		goldRatio = Double.parseDouble(props.getProperty(GOLD_RATIO_PROPERTY));
		workersPerObject = Integer.parseInt(props
				.getProperty(WORKERS_PER_OBJECT_PROPERTY));
	}

	private static void printHelp() {
		System.out.println("Troia test data generator parameters :");
		System.out.println('\t' + CONFIGURATION_FILE_TAG + SEPARATOR
				+ "Loads configuration from settings file");
		System.out.println('\t' + TEST_DATA_FILE_TAG + SEPARATOR + "base name for output files");
		System.out.println('\t'+CATEGORY_COUNT_TAG+SEPARATOR+"number of categories in test data");
		System.out.println('\t'+OBJECT_COUNT_TAG+SEPARATOR+"number of objects in test data");
		System.out.println('\t'+WORKER_COUNT_TAG+SEPARATOR+"number of workers in test data");
		System.out.println('\t'+MAX_QUALITY_TAG+SEPARATOR+"maximal quality of worker (from 0 to 1)");
		System.out.println('\t'+MIN_QUALITY_TAG+SEPARATOR+"minimal quality of worker (from 0 to 1)");
		System.out.println('\t'+WORKERS_PER_OBJECT_TAG+SEPARATOR+"number of workers assigned to single object");
		System.out.println('\t'+GOLD_RATIO_TAG+SEPARATOR+"ratio of gold labels among objects (from 0 to 1)");
		System.out.println('\t'+BASIC_WORKER_FILE_TAG+SEPARATOR+" name of file containing basic workers definition.");
	}

	
	private static final String CATEGORY_COUNT_TAG = "-c";
	private static final String OBJECT_COUNT_TAG = "-o";
	private static final String WORKER_COUNT_TAG = "-w";
	private static final String MAX_QUALITY_TAG = "-h";
	private static final String MIN_QUALITY_TAG = "-l";
	private static final String WORKERS_PER_OBJECT_TAG = "-p";
	private static final String GOLD_RATIO_TAG = "-g";
	private static final String BASIC_WORKER_FILE_TAG = "-q";
	private static final String CATEGORIES_FILE_TAG = "-a";
	private static final String TEST_DATA_FILE_TAG = "-t";
	private static final String CONFIGURATION_FILE_TAG = "-f";

	private static final String CATEGORY_COUNT_PROPERTY = "category_count";
	private static final String OBJECT_COUNT_PROPERTY = "object_count";
	private static final String WORKER_COUNT_PROPERTY = "worker_count";
	private static final String MINIMUM_QUALITY_PROPERTY = "minimal_worker_quality";
	private static final String MAXIMUM_QUALITY_PROPERY = "maximal_worker_quality";
	private static final String WORKERS_PER_OBJECT_PROPERTY = "workers_per_object";
	private static final String GOLD_RATIO_PROPERTY = "gold_ratio";

	private static final String SEPARATOR = " - ";

	private static int categoryCount = 0;
	private static int objectCount = 0;
	private static int workerCount = 0;
	private static double minQuality = 0;
	private static double maxQuality = 0;
	private static int workersPerObject = 0;
	private static double goldRatio = 0;
	private static String workerQualitiesFilename = null;
	private static String categoriesFileName = null;
	private static String outputFilename = "testData";
	private static Map<String, Double> categories;

	private static Data data = new Data();

	private static DataGenerator generator = DataGenerator
			.getInstance();
	private static DataManager manager = DataManager.getInstance();
}
