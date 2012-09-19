package com.datascience.gal.generator;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
		StringBuilder ret = new StringBuilder();
		ret.append("\n==============================Input=========================<<<<<<<<<<<<");
		ret.append("\nIterations: "+iterationsInt);
		ret.append("\n--------------------CATEGORIES WITH PROBABILITIES-----------------------");
		ret.append("\n"+categoryMap.toString());
		ret.append("\n");
		
		ret.append("\n-----------------------------WORKERS------------------------------------");
//		ret.append("\n"+workerMap.toString());
		for (String workerName:workerMap.keySet()) {
			ret.append("\n"+workerName.toString()+" ConfusionMatrix: \n" + Gen.print(workerMap.get(workerName)));
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

	/**
	 * @param w - array of Workers
	 * @param o - array of Objects
	 * @param c - array of Categories
	 * @param answerMatrix
	 * @return
	 */
	public InputConditionsForTest addAnswers(String[] w, String[] o,
			String[] c, int[][] answerMatrix) {
		for (int i=1; i< w.length; i++) {
			for (int j=0; j<o.length; j++) {
				addAnswers(w[i], o[j], c[answerMatrix[i][j]]);
			}
		}
		//the first employee gives right answers for all questions
		for(String obj: goldMap.keySet()) {
			addAnswers(w[0], obj, goldMap.get(obj));
		}
		//the last one employee gives wrong answers for all questions
		for(String obj: goldMap.keySet()) {
			addAnswers(w[w.length-1], obj, wrongAnswer(obj));
		}
		return this;
	}
	
	/**  
	 * @param objectName
	 * @return Wrong Category for the given Object
	 */
	private String wrongAnswer(String objectName) {
		String rightAnswer = goldMap.get(objectName);
		Set<String> categorySet = categoryMap.keySet();
		Random rand = new Random();
		int i=0;
		int tries = 10;
		String wrongAnswer = new String(rightAnswer);
		do {
			i = rand.nextInt(categorySet.size());
			wrongAnswer = (String) (categorySet.toArray())[i];
		} while (wrongAnswer.equals(rightAnswer) && tries-->0);
		return wrongAnswer;
	}

	/** Fills Gold List 
	 * @param o
	 * @param c
	 * @return
	 */
	public InputConditionsForTest addGolds(String[] o, String[] c) {
		int countOfReferencesOfTheCategoryArr[] = new int[c.length];
		int left = o.length;
		int i = 0;
		for (String category: categoryMap.keySet()) {
			int currentCount = Math.max(1, (int)(o.length*categoryMap.get(category)));
			countOfReferencesOfTheCategoryArr[i++] = currentCount;
			left-=currentCount;
		}
		countOfReferencesOfTheCategoryArr[c.length-1]+=left;
		int t = 0;
		for (int j=0; j<countOfReferencesOfTheCategoryArr.length; j++) {
//			System.out.println("["+j+"]"+countOfReferencesOfTheCategoryArr[j]);
			for (int k=0; k<countOfReferencesOfTheCategoryArr[j]; k++) {
				addGold(o[t++], c[j]);	
			}
			
		}
		return this;
	}

	/**
	 * @param w
	 * @param length
	 * @return
	 */
	public InputConditionsForTest addWorkers(String[] w, int length) {
		for (String workerName:w) {
			addWorker(workerName, Gen.generateCM(length));
		}
		return this;
	}
}
