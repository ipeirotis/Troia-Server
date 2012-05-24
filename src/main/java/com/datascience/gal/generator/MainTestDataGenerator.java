package com.datascience.gal.generator;


/**
 * @author Michael Arshynov
 *
 */
public class MainTestDataGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String c[] = {"notporn", "softporn", "hardporn"};
		String w[] = {"worker1", "worker2", "worker3", "worker4"};
		String o[] = {"url1", "url2", "url3"};
//		Double[][] cm = CVGen.generate(3);
		
		InputConditionsForTest input = 
				new InputConditionsForTest()
					.setIteration(10)
					
					.addCategory(c[0], 0.1)
					.addCategory(c[1], 0.4)
					.addCategory(c[2], 0.5)
		
					.addWorker(w[0], CVGen.generate(3))
					.addWorker(w[1], CVGen.generate(3))
					.addWorker(w[2], CVGen.generate(3))
					.addWorker(w[3], CVGen.generate(3))

					.addGold(o[0], c[0])
					.addGold(o[1], c[1])
					.addGold(o[2], c[2])
					
					.addAnswers(w[0], o[0], c[0]) 	//100% of right answers
					.addAnswers(w[0], o[1], c[1])
					.addAnswers(w[0], o[2], c[2])
					
					.addAnswers(w[1], o[0], c[0])	//67% of right answers
					.addAnswers(w[1], o[1], c[1])
					.addAnswers(w[1], o[2], c[0])
					
					.addAnswers(w[2], o[0], c[0])	//33% of right answers
					.addAnswers(w[2], o[1], c[2])
					.addAnswers(w[2], o[2], c[0])
					
					.addAnswers(w[3], o[0], c[1])	//0% of right answers
					.addAnswers(w[3], o[1], c[2])
					.addAnswers(w[3], o[2], c[0]);
		
		TestDatum test = new TestDatum();
		test.construct(input);
		System.out.println(input);
		System.out.println(test.compute());

	}

}
