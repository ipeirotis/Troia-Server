package gal.integration.tests;

import gal.integration.helpers.*;
import java.util.Collection;
import java.util.HashSet;
import org.junit.BeforeClass;
import com.datascience.gal.*;

public class Test_BatchDawidSkene_BarzanMozafari extends Tests{
	
	public static String TESTDATA_BASEDIR = System.getProperty("user.dir").concat("//src//test//java//gal//integration//datasets//");
	public static String RESULTS_BASEDIR = System.getProperty("user.dir").concat("//results//");
	public static String TEST_DIR = "BarzanMozafari";
	
	public static String inputDir 	= TESTDATA_BASEDIR + TEST_DIR + "//input//";
	public static String outputDir 	= TESTDATA_BASEDIR + TEST_DIR + "//output//";
	
	public static String CATEGORIES_FILE 	= inputDir 	+ 	"categories.txt";
	public static String COSTS_FILE 		= inputDir 	+ 	"costs.txt";
	public static String GOLDLABELS_FILE 	= inputDir 	+ 	"correct.txt";
	public static String LABELS_FILE 		= inputDir 	+ 	"input.txt";
	public static String SUMMARY_FILE 		= outputDir +	"summary.txt";
	
	//test results file
	public static String TEST_RESULTS_FILE = RESULTS_BASEDIR + "Results_BarzanMozafari.csv";
	public static String PROJECT_ID = "12345";
	
	static Collection<Category> categories;
	static HashSet<MisclassificationCost> misclassificationCosts;
	static Collection<CorrectLabel> correctLabels;
	static Collection<AssignedLabel> assignedLabels;
	static BatchDawidSkene ds;
	static TestHelpers testHelper;
	static SummaryResultsParser summaryResultsParser;
	static FileWriters fileWriter;
	
	static Tests.TestSetup testSetup;
	
	@BeforeClass
	public static void setupTests(){
		testHelper = new TestHelpers();

		//prepare the test results file
		fileWriter = new FileWriters();
		fileWriter.createNewFile(TEST_RESULTS_FILE);
		fileWriter.writeToFile(TEST_RESULTS_FILE, "Metric,GAL value,Troia value");
		
		summaryResultsParser = new SummaryResultsParser();
		
		categories = testHelper.LoadCategories(CATEGORIES_FILE);
		ds = new BatchDawidSkene(PROJECT_ID, categories);
				
		misclassificationCosts = testHelper.LoadMisclassificationCosts(COSTS_FILE);
		ds.addMisclassificationCosts(misclassificationCosts);
				
		correctLabels = testHelper.LoadGoldLabels(GOLDLABELS_FILE);
		ds.addCorrectLabels(correctLabels);
				
		assignedLabels = testHelper.LoadWorkerAssignedLabels(LABELS_FILE);
		ds.addAssignedLabels(assignedLabels);
		
		//ds.addEvaluationDatums(testHelper.LoadEvaluationLabels(evaluationLabelsFileName));
		//init the test setup
		testSetup = new Tests.TestSetup(ds, SUMMARY_FILE, TEST_RESULTS_FILE); 
		initSetup(testSetup);
	}

	
	
}
