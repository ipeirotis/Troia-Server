package com.datascience.galc.dataGenerator;

import java.io.IOException;

public class SyntheticDataGenerator {

	String optionsPath;
	
	public SyntheticDataGenerator() {
	
	}
	
	public SyntheticDataGenerator(String optionsPath) {
		this.optionsPath = optionsPath;
	}
	
	public void execute() throws IOException {
		SyntheticData data = createSyntheticDataSet(false,"data/synthetic/synthetic-options.txt");
		data.writeLabelsToFile("data/synthetic/assignedlabels.txt");
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
