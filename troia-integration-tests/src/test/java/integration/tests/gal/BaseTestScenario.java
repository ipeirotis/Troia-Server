package test.java.integration.tests.gal;

import com.datascience.core.algorithms.INewDataObserver;
import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.CategoryValue;
import com.datascience.core.nominal.NominalAlgorithm;
import com.datascience.core.nominal.NominalData;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.nominal.decision.*;
import com.datascience.core.results.WorkerResult;
import com.datascience.gal.Quality;
import com.datascience.gal.evaluation.DataEvaluator;
import com.datascience.gal.evaluation.WorkerEvaluator;
import com.datascience.utils.CostMatrix;
import test.java.integration.helpers.FileWriters;
import test.java.integration.helpers.SummaryResultsParser;
import test.java.integration.helpers.TestHelpers;
import test.java.integration.helpers.TestSettings;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BaseTestScenario {

    public final static String DATA_BASE_DIR = TestSettings.GAL_TESTDATA_BASEDIR;
    public final static String RESULTS_BASE_DIR = TestSettings.GAL_RESULTS_BASEDIR;
    protected static String inputDir;
    protected static String outputDir;
    protected static TestHelpers testHelper = new TestHelpers();
    protected static FileWriters fileWriter;
    protected static SummaryResultsParser summaryResultsParser;
    protected static NominalProject project;
    protected static NominalData data;

    public static void setUp(NominalAlgorithm algorithm, String testName, boolean loadEvaluationLabels) {
        project = new NominalProject(algorithm);
        data = project.getData();
        if (algorithm instanceof INewDataObserver) {
            data.addNewUpdatableAlgorithm((INewDataObserver) algorithm);
        }
        inputDir = DATA_BASE_DIR + testName + TestSettings.FILEPATH_SEPARATOR + "input" + TestSettings.FILEPATH_SEPARATOR;
        outputDir = DATA_BASE_DIR + testName + TestSettings.FILEPATH_SEPARATOR + "output" + TestSettings.FILEPATH_SEPARATOR;
        fileWriter = new FileWriters(RESULTS_BASE_DIR + "Results_" + algorithm.getClass().getSimpleName() + "_" + testName + ".csv");
        summaryResultsParser = new SummaryResultsParser(outputDir + "summary.txt");
        loadData(loadEvaluationLabels);
        project.getAlgorithm().compute();
        fileWriter.write("Metric,GAL value,Troia value");
    }

    public static void loadData(boolean loadEvaluationLabels) {
        loadCategories();
        loadAssignedLabels();
        loadGoldLabels(); 
        if (loadEvaluationLabels){
            loadEvaluationLabels();
        }
        
    }

    public static void loadCategories() {
        Collection<String> categories = testHelper.LoadCategories(inputDir + "categories.txt");
        Collection<CategoryValue> priors = testHelper.loadCategoryPriors(inputDir + "categories.txt");
        CostMatrix<String> costs = testHelper.loadCostsMatrix(inputDir + "costs.txt");
        project.initializeCategories(categories, priors, costs);
    }

    public static void loadGoldLabels() {
        Collection<LObject<String>> goldLabels = testHelper.LoadGoldLabels(inputDir + "correct.txt");
        for (LObject<String> goldLabel : goldLabels) {
            data.addObject(goldLabel);
        }
    }

    public static void loadEvaluationLabels() {
        Collection<LObject<String>> evaluationLabels = testHelper.LoadEvaluationLabels(inputDir + "evaluation.txt");
        for (LObject<String> evaluationLabel : evaluationLabels) {
            data.addObject(evaluationLabel);
        }
    }

    public static void loadAssignedLabels() {
        Collection<AssignedLabel<String>> assignedLabels = testHelper.LoadWorkerAssignedLabels(inputDir + "input.txt");
        for (AssignedLabel<String> assign : assignedLabels) {
            Worker<String> worker = data.getOrCreateWorker(assign.getWorker().getName());
            data.addWorker(worker);
            assign.setWorker(worker);
            LObject<String> object = data.getOrCreateObject(assign.getLobject().getName());
            data.addObject(object);
            assign.setLobject(object);
        }
        for (AssignedLabel<String> assign : assignedLabels) {
            data.addAssign(assign);
        }
    }

    public void setFileWriter(FileWriters fileWriter) {
        this.fileWriter = fileWriter;
    }

    public void setSummaryResultsParser(SummaryResultsParser summaryResultsParser) {
        this.summaryResultsParser = summaryResultsParser;
    }

    public double estimateMissclassificationCost(
            ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator,
            IObjectLabelDecisionAlgorithm objectLabelDecisionAlgorithm) {
        DecisionEngine decisionEngine = new DecisionEngine(labelProbabilityDistributionCostCalculator,
                objectLabelDecisionAlgorithm);
        Set<LObject<String>> objects = data.getObjects();
        double avgClassificationCost = 0.0;

        //compute the estimated misclassification cost for each object, using DS
        for (LObject<String> object : objects) {
            avgClassificationCost += decisionEngine.estimateMissclassificationCost(project, object);
        }

        //calculate the average
        avgClassificationCost = avgClassificationCost / objects.size();
        return avgClassificationCost;
    }

    public double evaluateMissclassificationCost(String labelChoosingMethod) {
        DataEvaluator dataEvaluator = new DataEvaluator(labelChoosingMethod);

        //compute the evaluated misclassification cost
        Map<String, Double> evaluated = dataEvaluator.evaluate(project);

        double avgCost = 0.0;

        for (Map.Entry<String, Double> entry : evaluated.entrySet()) {
            avgCost += entry.getValue();
        }

        //calculate the average cost
        avgCost = avgCost / data.getEvaluationObjects().size();
        return avgCost;
    }

    public double estimateCostToQuality(ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator,
            IObjectLabelDecisionAlgorithm objectLabelDecisionAlgorithm) {
        DecisionEngine decisionEngine = new DecisionEngine(labelProbabilityDistributionCostCalculator, objectLabelDecisionAlgorithm);
        Map<String, Double> costQuality = Quality.fromCosts(project, decisionEngine.estimateMissclassificationCosts(project));

        double avgQuality = 0.0;

        //compute the estimated quality cost for each object, using MV
        for (Map.Entry<String, Double> cQuality : costQuality.entrySet()) {
            avgQuality += cQuality.getValue();
        }

        //calculate the average
        avgQuality /= costQuality.size();
        return avgQuality;
    }

    public double evaluateCostToQuality(String labelChoosingMethod) {
        DataEvaluator dataEvaluator = new DataEvaluator(labelChoosingMethod);

        Map<String, Double> costQuality = Quality.fromCosts(project, dataEvaluator.evaluate(project));
        double avgQuality = 0.0;

        //compute the estimated quality cost for each object, using MV
        for (Map.Entry<String, Double> cQuality : costQuality.entrySet()) {
            avgQuality += cQuality.getValue();
        }

        //calculate the average
        avgQuality /= costQuality.size();
        return avgQuality;
    }

    public double estimateWorkerQuality(String method, String estimationType) {
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator =
                LabelProbabilityDistributionCostCalculators.get(method);
        WorkerEstimator workerEstimator = new WorkerEstimator(labelProbabilityDistributionCostCalculator);
        Map<String, Double> result = new HashMap<String, Double>();
        Map<String, Integer> workerAssignedLabels = new HashMap<String, Integer>();

        for (Map.Entry<Worker<String>, WorkerResult> w : project.getResults().getWorkerResults().entrySet()) {
            Worker<String> worker = w.getKey();
            result.put(worker.getName(), workerEstimator.getCost(project, worker));
            workerAssignedLabels.put(worker.getName(), project.getData().getWorkerAssigns(worker).size());
        }

        Map<String, Double> workersQuality = Quality.fromCosts(project, result);
        double quality = 0.0;

        if (estimationType.equals("n")) {
            //compute the non weighted worker quality
            for (Map.Entry<String, Double> workerQuality : workersQuality.entrySet()) {
                quality += workerQuality.getValue();
            }
            //calculate the average
            quality /= workersQuality.size();
            return quality;
        } else {
            //compute the weighted worker quality
            int totalNoLabels = 0;
            for (Map.Entry<String, Double> workerQuality : workersQuality.entrySet()) {
                quality += workerQuality.getValue() * workerAssignedLabels.get(workerQuality.getKey());
                totalNoLabels += workerAssignedLabels.get(workerQuality.getKey());
            }
            quality /= totalNoLabels;
            return quality;
        }
    }

    public double evaluateWorkerQuality(String method, String estimationType) {
        ILabelProbabilityDistributionCostCalculator labelProbabilityDistributionCostCalculator =
                LabelProbabilityDistributionCostCalculators.get(method);
        WorkerEvaluator workerEvaluator = new WorkerEvaluator(labelProbabilityDistributionCostCalculator);
        Map<String, Double> result = new HashMap<String, Double>();
        Map<String, Integer> workerAssignedLabels = new HashMap<String, Integer>();

        for (Map.Entry<Worker<String>, WorkerResult> w : project.getResults().getWorkerResults().entrySet()) {
            Worker<String> worker = w.getKey();
            result.put(worker.getName(), workerEvaluator.getCost(project, worker));
            workerAssignedLabels.put(worker.getName(), project.getData().getWorkerAssigns(worker).size());
        }

        Map<String, Double> workersQuality = Quality.fromCosts(project, result);
        double quality = 0.0;
        double denominator = 0.;

        if (estimationType.equals("n")) {
            //compute the non-weighted worker quality
            for (Map.Entry<String, Double> workerQuality : workersQuality.entrySet()) {
                Double val = workerQuality.getValue();
                if (val == null || val.isNaN()) {
                    continue;
                }
                quality += val;
                denominator += 1.;
            }
            //calculate the average
            quality /= denominator;
            return quality;
        } else {
            //compute the weighted worker quality
            int totalNoLabels = 0;
            for (Map.Entry<String, Double> workerQuality : workersQuality.entrySet()) {
                Double val = workerQuality.getValue();
                if (val == null || val.isNaN()) {
                    continue;
                }

                quality += val * workerAssignedLabels.get(workerQuality.getKey());
                totalNoLabels += workerAssignedLabels.get(workerQuality.getKey());
            }
            denominator += totalNoLabels;
            quality /= denominator;
            return quality;
        }
    }
}
