package gal.integration.helpers;

import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import com.datascience.gal.dataGenerator.DataManager;
import com.datascience.gal.AssignedLabel;
import com.datascience.gal.Category;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.MisclassificationCost;

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
	
	/**
	 * Loads the categories and probabilities from the given file
	 * @param categoriesFileName
	 * @return Collection<Category>
	 */
	public Collection<Category> LoadCategories(String categoriesFileName){
		//Load the categories and probabilities file from CATEGORIES_FILE 
		Map<String, Double> categoryNamesProbsMap =  new HashMap<String, Double>();
		
		try{
			categoryNamesProbsMap = dataManager.loadCategoriesWithProbabilities(categoriesFileName);
		}
		catch (FileNotFoundException ex){
			ex.printStackTrace();
		}
				 
		Collection<Category> categories = new ArrayList<Category>();	 
		for (Map.Entry<String, Double> entry : categoryNamesProbsMap.entrySet()) {
			Category category  = new Category(entry.getKey());
			category.setPrior(entry.getValue());
			categories.add(category);
		}
		return categories;	
	}
	
	/**
	 * Loads the misclassification costs from the given file
	 * @param misclassificationCostFileName
	 * @return HashSet<MisclassificationCost>
	 */
	public HashSet<MisclassificationCost> LoadMisclassificationCosts(String misclassificationCostFileName){
		HashSet<MisclassificationCost> misclassificationCosts = new HashSet<MisclassificationCost>();
		
		try{
			misclassificationCosts = (HashSet<MisclassificationCost>) fileReader.loadMisclassificationCostData(misclassificationCostFileName);
		}
		catch (FileNotFoundException ex){
			ex.printStackTrace();
		}	
		return misclassificationCosts;
	}
	
	/**
	 * Loads the gold labels from the given file
	 * @param goldLabelsFileName
	 * @return Collection<CorrectLabel>
	 */
	public Collection<CorrectLabel> LoadGoldLabels(String goldLabelsFileName){
		Collection<CorrectLabel> goldLabels = new ArrayList<CorrectLabel>();
		
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
	public Collection<CorrectLabel> LoadEvaluationLabels(String evaluationLabelsFileName){
		return LoadGoldLabels(evaluationLabelsFileName);
	}
	
	/**
	 * Loads the worker assigned labels from the given file
	 * @param labelsFileName
	 * @return Collection <Label>
	 */
	public Collection<AssignedLabel> LoadWorkerAssignedLabels(String labelsFileName){
		Collection <AssignedLabel> assignedLabels = new ArrayList<AssignedLabel>();
		
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
