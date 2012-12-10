package com.datascience.gal.generator;


/**
 * @author Michael Arshynov
 *
 */
public class OldMainTestDataGenerator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		String c[] = {"notporn", "softporn", "hardporn"};
		String w[] = {"worker1", "worker2", "worker3", "worker4", "worker5"};
		String o[] = {"url1", "url2", "url3", "url4", "url5", "url6", "url7", "url8", "url9","url10", "url11", "url12", "url13", "url14", "url15"};
//		Double[][] cm = CVGen.generate(3);

//		int answerMatrix[][][] = {{{0},{0},{0}},{{0},{0},{0}},{{0},{0},{0}}};

		int answerMatrix[][] = Gen.generateAM(5,15,3);


		InputConditionsForTest input =
			new InputConditionsForTest()
		.setIteration(10)

//					.addCategory(c[0], 0.1)
//					.addCategory(c[1], 0.4)
//					.addCategory(c[2], 0.5)
		.addCategory(c[0], 0.14) // 1/7
		.addCategory(c[1], 0.29) // 2/7
		.addCategory(c[2], 0.57) // 4/7

//					.addWorker(w[0], Gen.generateCM(7))
//					.addWorker(w[1], Gen.generateCM(7))
//					.addWorker(w[2], Gen.generateCM(7))
//					.addWorker(w[3], Gen.generateCM(7))
//					.addWorker(w[4], Gen.generateCM(7))
		.addWorkers(w, o.length)
//					.addGold(o[0], c[0])
//					.addGold(o[1], c[1])
//					.addGold(o[2], c[1])
//					.addGold(o[3], c[2])
//					.addGold(o[4], c[2])
//					.addGold(o[5], c[2])
//					.addGold(o[6], c[2])
		.addGolds(o, c)
		/*
					.addAnswers(w[0], o[0], c[0]) 	//100% of right answers, ANSW 0.0%
					.addAnswers(w[0], o[1], c[1])
					.addAnswers(w[0], o[2], c[1])
					.addAnswers(w[0], o[3], c[2])
					.addAnswers(w[0], o[4], c[2])
					.addAnswers(w[0], o[5], c[2])
					.addAnswers(w[0], o[6], c[2])

					.addAnswers(w[1], o[0], c[2])	//  5/7 of right answers, 71% ANSW 14.12%
					.addAnswers(w[1], o[1], c[1])
					.addAnswers(w[1], o[2], c[1])
					.addAnswers(w[1], o[3], c[2])
					.addAnswers(w[1], o[4], c[2])
					.addAnswers(w[1], o[5], c[2])
					.addAnswers(w[1], o[6], c[0])

					.addAnswers(w[2], o[0], c[0])	// 3/7 of right answers, 43% ANSW 28.75%
					.addAnswers(w[2], o[1], c[2])
					.addAnswers(w[2], o[2], c[2])
					.addAnswers(w[2], o[3], c[2])
					.addAnswers(w[2], o[4], c[1])
					.addAnswers(w[2], o[5], c[1])
					.addAnswers(w[2], o[6], c[2])

					.addAnswers(w[3], o[0], c[2])	//0% of right answers, ANSW 50%
					.addAnswers(w[3], o[1], c[2])
					.addAnswers(w[3], o[2], c[2])
					.addAnswers(w[3], o[3], c[0])
					.addAnswers(w[3], o[4], c[0])
					.addAnswers(w[3], o[5], c[1])
					.addAnswers(w[3], o[6], c[1])

		.addAnswers(w[4], o[0], c[0])	//1/7 of right answers, 14% ANSWER, ANSW 43%
		.addAnswers(w[4], o[1], c[0])
		.addAnswers(w[4], o[2], c[0])
		.addAnswers(w[4], o[3], c[0])
		.addAnswers(w[4], o[4], c[0])
		.addAnswers(w[4], o[5], c[0])
		.addAnswers(w[4], o[6], c[0]);		*/
		.addAnswers(w, o, c, answerMatrix);


//		input.addGolds(o, c);
		TestDatum test = new TestDatum();
		test.construct(input);
		System.out.println(input);
		System.out.println(
			test.compute()
		);

	}

}
