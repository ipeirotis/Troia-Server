package test.java.integration.helpers;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.nominal.CategoryValue;
import com.datascience.gal.dataGenerator.DataManager;
import com.datascience.utils.CostMatrix;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;


public class TestHelpers {
	
	DataManager dataManager = DataManager.getInstance();
	FileReaders fileReader = new FileReaders();
	
	public String format(Double result) {
		int decimalPlace = 4;
		if (null == result || Double.isNaN(result))
			return "N/A";
		
		BigDecimal bd = new BigDecimal(Double.toString(result));
		bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
		return String.format(Locale.ENGLISH, "%2.4f",bd.doubleValue());
	}
	
	
	public String formatPercent(Double result) {
		if (null == result || Double.isNaN(result)){
			return "N/A";
		}
		else{
			NumberFormat percentFormat = NumberFormat.getPercentInstance();
			percentFormat.setMinimumFractionDigits(2);
			percentFormat.setMaximumFractionDigits(2);
			return percentFormat.format(result);
		}
	}
	/**
	 * Loads the categories and probabilities from the given file
	 * @param categoriesFileName
	 * @return Collection<Category>
	 */
	public Collection<String> LoadCategories(String categoriesFileName){
		//Load the categories and probabilities file from CATEGORIES_FILE 
		Map<String, Double> categoryNamesProbsMap =  new HashMap<String, Double>();
		
		try{
			categoryNamesProbsMap = dataManager.loadCategoriesWithProbabilities(categoriesFileName);
		}
		catch (FileNotFoundException ex){
			ex.printStackTrace();
		}
				 
		Collection<String> categories = new ArrayList<String>();
		for (Map.Entry<String, Double> entry : categoryNamesProbsMap.entrySet()) {
			categories.add(entry.getKey());
		}
		return categories;	
	}

	public Collection<CategoryValue> loadCategoryPriors(String fn){
		Map<String, Double> categoryNamesProbsMap =  new HashMap<String, Double>();

		try{
			categoryNamesProbsMap = dataManager.loadCategoriesWithProbabilities(fn);
		}
		catch (FileNotFoundException ex){
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
	 * @param misclassificationCostFileName
	 * @return HashSet<MisclassificationCost>
	 */
	public CostMatrix<String> loadCostsMatrix(String misclassificationCostFileName){
		try{
			return fileReader.loadCostMatrix(misclassificationCostFileName);
		}
		catch (FileNotFoundException ex){
			ex.printStackTrace();
		}	
		return null;
	}
	
	/**
	 * Loads the gold labels from the given file
	 * @param goldLabelsFileName
	 * @return Collection<CorrectLabel>
	 */
	public Collection<LObject<String>> LoadGoldLabels(String goldLabelsFileName){
		Collection<LObject<String>> goldLabels = new ArrayList<LObject<String>>();
		
		try{
			goldLabels = dataManager.loadGoldLabelsFromFile(goldLabelsFileName);
		}
		catch (FileNotFoundException ex){
			ex.printStackTrace();
		}
		
		return goldLabels;
	}

	/**
	 * Loads the evaluation labels from the given file
	 * @param evaluationLabelsFileName
	 * @return Collection<CorrectLabel>
	 */
	public Collection<LObject<String>> LoadEvaluationLabels(String evaluationLabelsFileName){
		Collection<LObject<String>> goldLabels = new ArrayList<LObject<String>>();

		try{
			goldLabels = dataManager.loadEvaluationLabelsFromFile(evaluationLabelsFileName);
		}
		catch (FileNotFoundException ex){
			ex.printStackTrace();
		}

		return goldLabels;
	}
	
	/**
	 * Loads the evaluation labels from the given file
	 * @param evaluationLabelsFileName
	 * @return Collection<CorrectLabel>
	 */
//	public Collection<LObject<String>> LoadEvaluationLabels(String evaluationLabelsFileName){
//		return LoadGoldLabels(evaluationLabelsFileName);
//	}
	
	/**
	 * Loads the worker assigned labels from the given file
	 * @param labelsFileName
	 * @return Collection <Label>
	 */
	public Collection<AssignedLabel<String>> LoadWorkerAssignedLabels(String labelsFileName){
		Collection <AssignedLabel<String>> assignedLabels = new ArrayList<AssignedLabel<String>>();
		
		try{
			assignedLabels = dataManager.loadLabelsFromFile(labelsFileName);
		}
		catch (FileNotFoundException ex){
			ex.printStackTrace();
		}
		
		return assignedLabels;
	}
	
	
	public LinkedList<Map<String, Object>> LoadWorkerSummaryFile(String expectedSummaryFileName){
		LinkedList<Map<String, Object>>	expectedWorkerScores = new LinkedList<Map<String, Object>>();
		
		try{
			expectedWorkerScores = fileReader.loadWorkerSummaryFile(expectedSummaryFileName);
		}
		catch (FileNotFoundException ex){
			ex.printStackTrace();
		}
		
		return expectedWorkerScores;
		
	}
}
