package test.java.integration.helpers;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.nominal.CategoryValue;
import com.datascience.gal.dataGenerator.DataManager;
import com.datascience.utils.CostMatrix;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class TestHelpers {

    DataManager dataManager = DataManager.getInstance();
    FileReaders fileReader = new FileReaders();

    public Double format(Double result) {
        if (Double.isNaN(result)) {
            return Double.NaN;
        }

        DecimalFormat df = new DecimalFormat("#.0000");
        return Double.valueOf(df.format(result));
    }

    public Double formatPercent(Double result) {
        if (null == result || Double.isNaN(result)) {
            return Double.NaN;
        } else {
            DecimalFormat df = new DecimalFormat("#.00");
            return Double.valueOf(df.format(result));
        }
    }

    /**
     * Loads the categories and probabilities from the given file
     *
     * @param categoriesFileName
     * @return Collection<Category>
     */
    public Collection<String> LoadCategories(String categoriesFileName) {
        //Load the categories and probabilities file from CATEGORIES_FILE 
        Map<String, Double> categoryNamesProbsMap = new HashMap<String, Double>();

        try {
            categoryNamesProbsMap = dataManager.loadCategoriesWithProbabilities(categoriesFileName);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        Collection<String> categories = new ArrayList<String>();
        for (Map.Entry<String, Double> entry : categoryNamesProbsMap.entrySet()) {
            categories.add(entry.getKey());
        }
        return categories;
    }

    public Collection<CategoryValue> loadCategoryPriors(String fn) {
        Map<String, Double> categoryNamesProbsMap = new HashMap<String, Double>();

        try {
            categoryNamesProbsMap = dataManager.loadCategoriesWithProbabilities(fn);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        Collection<CategoryValue> priors = new ArrayList<CategoryValue>();
        for (Map.Entry<String, Double> entry : categoryNamesProbsMap.entrySet()) {
            priors.add(new CategoryValue(entry.getKey(), entry.getValue()));
        }
        return priors;
    }

    /**
     * Loads the misclassification costs from the given file
     *
     * @param misclassificationCostFileName
     * @return HashSet<MisclassificationCost>
     */
    public CostMatrix<String> loadCostsMatrix(String misclassificationCostFileName) {
        try {
            return fileReader.loadCostMatrix(misclassificationCostFileName);
        } catch (FileNotFoundException ex) {
            System.err.println("No cost matrix defined");
        }
        return null;
    }

    /**
     * Loads the gold labels from the given file
     *
     * @param goldLabelsFileName
     * @return Collection<CorrectLabel>
     */
    public Collection<LObject<String>> LoadGoldLabels(String goldLabelsFileName) {
        Collection<LObject<String>> goldLabels = new ArrayList<LObject<String>>();

        try {
            goldLabels = dataManager.loadGoldLabelsFromFile(goldLabelsFileName);
        } catch (FileNotFoundException ex) {
             System.err.println("No gold labels file");
        }

        return goldLabels;
    }

    /**
     * Loads the evaluation labels from the given file
     *
     * @param evaluationLabelsFileName
     * @return Collection<CorrectLabel>
     */
    public Collection<LObject<String>> LoadEvaluationLabels(String evaluationLabelsFileName) {
        Collection<LObject<String>> goldLabels = new ArrayList<LObject<String>>();

        try {
            goldLabels = dataManager.loadEvaluationLabelsFromFile(evaluationLabelsFileName);
        } catch (FileNotFoundException ex) {
            System.err.println("No evaluation labels file");
        }

        return goldLabels;
    }

    /*
     * Loads the worker assigned labels from the given file
     *
     * @param labelsFileName
     * @return Collection <Label>
     */
    public Collection<AssignedLabel<String>> LoadWorkerAssignedLabels(String labelsFileName) {
        Collection<AssignedLabel<String>> assignedLabels = new ArrayList<AssignedLabel<String>>();

        try {
            assignedLabels = dataManager.loadLabelsFromFile(labelsFileName);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        return assignedLabels;
    }

    public LinkedList<Map<String, Object>> LoadWorkerSummaryFile(String expectedSummaryFileName) {
        LinkedList<Map<String, Object>> expectedWorkerScores = new LinkedList<Map<String, Object>>();

        try {
            expectedWorkerScores = fileReader.loadWorkerSummaryFile(expectedSummaryFileName);
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

        return expectedWorkerScores;

    }
}
