package com.datascience.galc.dataGenerator.engine;

import com.datascience.galc.dataGenerator.SyntheticData;
import java.io.IOException;

public class Engine {
	
	private EngineContext ctx;
	
	public Engine(EngineContext ctx) {
		
		this.ctx = ctx;
		
	}
	public void execute() throws IOException {

		SyntheticData data = createSyntheticDataSet(ctx.isVerbose(),ctx.getSyntheticOptionsFile());
		data.writeLabelsToFile(ctx.getLabelsFile());
		if(ctx.hasEvalWorkersFile())
			data.writeTrueWorkerDataToFile(ctx.getEvalWorkersFile());
		if(ctx.hasEvalobjectsFile())
			data.writeTrueObjectDataToFile(ctx.getEvalObjectsFile());
		if(ctx.hasGoldFile())
			data.writeGoldObjectDataToFile(ctx.getGoldFile());
		
	}

	/**
	 * @return
	 */
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
