package com.datascience.gal.generator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Michael Arshynov
 *
 */
public class InputConditionsForTest {
	@SuppressWarnings("unused")
	private int iterationsInt = 1;
	private Map<String, Double> categoryMap = null;
	private Map<String, Double[][]> workerMap = null;
	private Map<String, String> goldMap = null;
	private Map<String, Map<String, String>> answerMap = null;
	
	/**
	 * @param categoryName
	 * @param probability
	 * @return
	 */
	public InputConditionsForTest addCategory(String categoryName, double probability) {
		if (categoryMap == null) 
			categoryMap = new LinkedHashMap<String, Double>();
		categoryMap.put(categoryName, probability);
		return this;
	}
	
	public int getIterationsInt() {
		return iterationsInt;
	}

	public Map<String, Double> getCategoryMap() {
		return categoryMap;
	}

	public Map<String, Double[][]> getWorkerMap() {
		return workerMap;
	}

	public Map<String, String> getGoldMap() {
		return goldMap;
	}

	public Map<String, Map<String, String>> getAnswerMap() {
		return answerMap;
	}

	/**
	 * @param iterationsInt
	 * @return
	 */
	public InputConditionsForTest setIteration(int iterationsInt) {
		this.iterationsInt = iterationsInt;
		return this;
	}
	
	/**
	 * @param workerName
	 * @param confusionMatrix
	 * @return
	 */
	public InputConditionsForTest addWorker(String workerName, Double[][] confusionMatrix) {
		if (workerMap == null)
			workerMap = new LinkedHashMap<String, Double[][]>();  
		workerMap.put(workerName, confusionMatrix);
		return this;
	}
	
	/**
	 * @param labelName
	 * @param categoryName
	 * @return
	 */
	public InputConditionsForTest addGold(String labelName, String categoryName) {
		if (goldMap == null) 
			goldMap = new LinkedHashMap<String, String>();
		goldMap.put(labelName, categoryName);
		return this;
	}
	

	/**
	 * addAssignedLabel, shorter form of name
	 * @param workerName
	 * @param objectName
	 * @param categoryName
	 * @return
	 */
	public InputConditionsForTest addAnswers(String workerName, String objectName, String categoryName) {
		if (answerMap == null) 
			answerMap = new HashMap<String, Map<String,String>>();
		if (answerMap.containsKey(workerName)) {
			 answerMap.get(workerName).put(objectName, categoryName);
		} else {
			Map<String,String> val = new HashMap<String,String>();
			val.put(objectName, categoryName);
			answerMap.put(workerName, val);
		}
		return this;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		categoryMap
	}

	@Override
	public String toString() {
//		private Map<String, Double> categoryMap = null;
//		private Map<String, Double[][]> workerMap = null;
//		private Map<String, String> goldMap = null;
//		private Map<String, Map<String, String>> answerMap = null;
		StringBuilder ret = new StringBuilder();
		ret.append("\n==============================Input=========================<<<<<<<<<<<<");
		
		ret.append("\n--------------------CATEGORIES WITH PROBABILITIES-----------------------");
		ret.append("\n"+categoryMap.toString());
		ret.append("\n");
		
		ret.append("\n-----------------------------WORKERS------------------------------------");
//		ret.append("\n"+workerMap.toString());
		for (String workerName:workerMap.keySet()) {
			ret.append("\n"+workerName.toString()+" ConfusionMatrix: \n" + CVGen.print(workerMap.get(workerName)));
		}
		ret.append("\n");
		
		ret.append("\n---------------------------GOLD LABELS----------------------------------");
		ret.append("\n"+goldMap.toString());
		ret.append("\n");
		
		ret.append("\n------------------------------ANSWERS-----------------------------------");
		ret.append("\n");
//		ret.append("\n"+answerMap.toString());
		for (String workerName:answerMap.keySet()) {
			Map<String, String> answers = answerMap.get(workerName);
			ret.append(workerName+":\n");
			for (String answer:answers.keySet()) {
				ret.append("          "+answer+", "+answers.get(answer));
			}
			ret.append("\n");
		}
		
		
		ret.append("\n>>>>>>>>>>>>===================Input====================================");
		return ret.toString();
	}
}
