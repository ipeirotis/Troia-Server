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
	
	private static SyntheticData createSyntheticDataSet(Boolean verbose, String file) {

		SyntheticData data = new SyntheticData(verbose,file);

		data.initDataParameters();

		data.initWorkerParameters();

		data.build();
		return data;
	}
}
