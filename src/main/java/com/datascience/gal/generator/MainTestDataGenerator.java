package com.datascience.gal.generator;

import java.util.Date;

public class MainTestDataGenerator {
//	private String c[] = {"notporn", "softporn", "hardporn"};
	private String c[] = null; //{"sport", "business", "private life", "family events", "charity"};
	private String w[] = null;
	private String o[] = null;
	private InputConditionsForTest input = null;
	private int cOUNT_OF_THE_WORKERS;
	private int cOUNT_OF_THE_LABELS;
	private int iteration = 10;
	private int answerMatrix[][];
	/**
	 * @param cOUNT_OF_THE_WORKERS
	 * @param cOUNT_OF_THE_LABELS
	 * @param cOUNT_OF_THE_CATEGORIES 
	 */
	public MainTestDataGenerator(int cOUNT_OF_THE_WORKERS,
			int cOUNT_OF_THE_LABELS, int cOUNT_OF_THE_CATEGORIES) {
		this.cOUNT_OF_THE_WORKERS = cOUNT_OF_THE_WORKERS;
		this.cOUNT_OF_THE_LABELS = cOUNT_OF_THE_LABELS;
		answerMatrix = Gen.generateAM(cOUNT_OF_THE_WORKERS,cOUNT_OF_THE_LABELS,cOUNT_OF_THE_CATEGORIES);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final int COUNT_OF_THE_WORKERS = 10;
		final int COUNT_OF_THE_LABELS = 200;
		final int COUNT_OF_THE_CATEGORIES  = 5;
		
		MainTestDataGenerator testDataGen = new MainTestDataGenerator(COUNT_OF_THE_WORKERS,
				COUNT_OF_THE_LABELS, COUNT_OF_THE_CATEGORIES);
//		testDataGen.setCategories( new String[]{"notporn", "softporn", "hardporn"})
//		.setCategoriesProbs( new double[]{ 0.14, 0.29, 0.57})
		testDataGen.setCategories(new String[] {"sport", "business", "private life", "family events", "charity"})
		.setCategoriesProbs( new double[]{ 0.2, 0.2, 0.2, 0.2, 0.2})		
		;
		
		testDataGen
		.compute();
	}

	/**
	 * @return
	 */
	public MainTestDataGenerator addAnswers() {
		if (w.length >1 && o.length >1 && c.length >1 && answerMatrix.length >1 )
		input.addAnswers(w, o, c, answerMatrix);
		return this;
	}

	/**
	 * @return
	 */
	public MainTestDataGenerator addGolds() {
		if (o.length>1 && c.length>1)
			input.addGolds(o, c);
		else return null;
		return this;
	}

	/**
	 * @return
	 */
	public MainTestDataGenerator addWorkers() {
		w = Gen.generateLabelNameList("worker", cOUNT_OF_THE_WORKERS);
		if (o.length>1 && w.length>1)
			input.addWorkers(w, o.length);
		else return null;
		return this;
	}

	/**
	 * @return
	 */
	public MainTestDataGenerator addLabels() {
		o = Gen.generateLabelNameList("url", cOUNT_OF_THE_LABELS);
		return this;
	}

	/**
	 * @param cp - array of categories probabilities
	 * @return
	 */
	public MainTestDataGenerator setCategoriesProbs(double[] cp) {
		if (input == null) input = new InputConditionsForTest();
		if (cp.length == c.length && c.length>1) {
			for (int i=0; i<c.length; i++) 
				input.addCategory(c[i], cp[i]);
		} else return null;
		return this;
	}

	/**
	 * @param iteration
	 * @return
	 */
	public MainTestDataGenerator setIteration(int iteration) {
		if (input == null) input = new InputConditionsForTest();
		this.iteration = iteration;
		input.setIteration(iteration);
		return this;
	}
	
	/**
	 * @param categories
	 * @return
	 */
	public MainTestDataGenerator setCategories(String[] categories) {
		if (input == null) input = new InputConditionsForTest();
		if (c==null) c = categories;
		return this;
	}

	/**
	 * 
	 */
	public void compute() {
		long startDate = new Date().getTime();
		this.setIteration(iteration)
		.addLabels()
		.addWorkers()
		.addGolds()
		.addAnswers();
		
		TestDatum test = new TestDatum();
		test.construct(input);
//		System.out.println(input);
//		System.out.println(
				String out = test.compute().toString()
//				)
				;
				
		long endDate = new Date().getTime();		
				System.out.println(input);
				System.out.println(out);
		long endDateAfterPrint = new Date().getTime();
		System.out.println("Execution time: "+(endDateAfterPrint-startDate)+" msec, without results printing "+(endDate-startDate)+" msec");
	}
}
