package com.datascience.galc.dataGenerator;

import com.datascience.galc.dataGenerator.SyntheticData;

public class SyntheticDataGenerator {

	public SyntheticDataGenerator() {
		execute();
	}
	
	public void execute() {
		SyntheticData data = createSyntheticDataSet(false,"data/synthetic/synthetic-options.txt");
		data.writeLabelsToFile("data/synthetic/assignedlabels.txt");
		data.writeTrueWorkerDataToFile("data/synthetic/evaluationWorkers.txt");
		data.writeTrueObjectDataToFile("data/synthetic/evaluationObjects.txt");
		data.writeGoldObjectDataToFile("data/synthetic/goldObjects.txt");
	}	
	
	private static SyntheticData createSyntheticDataSet(boolean verbose, String file) {

		// int data_points = 10000;
		// Double data_mu = 7.0;
		// Double data_sigma = 11.0;
		// int data_gold = 100;
		//
		// int workers = 1;
		// Double worker_mu_down = -5.0;
		// Double worker_mu_up = 5.0;
		// Double worker_sigma_down = 0.5;
		// Double worker_sigma_up = 1.5;
		// Double worker_rho_down = 0.5;
		// Double worker_rho_up = 1.0;

		SyntheticData data = createDataSet(verbose,file);
		return data;
	}
	private static SyntheticData createDataSet(Boolean verbose, String file) {

		SyntheticData data = new SyntheticData(verbose,file);

		data.initDataParameters();

		data.initWorkerParameters();

		data.build();
		return data;
	}
	
}
