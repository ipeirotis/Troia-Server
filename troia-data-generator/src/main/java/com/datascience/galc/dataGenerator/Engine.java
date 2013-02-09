package com.datascience.galc.dataGenerator;

import java.io.IOException;
import org.apache.log4j.Logger;


public class Engine {
	
	private EngineContext ctx;
	private static Logger logger = Logger.getLogger(Engine.class);
	
	public Engine(EngineContext ctx) {
		this.ctx = ctx;
	}
	
	public void execute() throws IOException {

		SyntheticDataGenerator dataGenerator = new SyntheticDataGenerator(ctx.getSyntheticOptionsFile());
		SyntheticData data = dataGenerator.generate();
		SyntheticDataWriter dataWriter = new SyntheticDataWriter((data));

		if(ctx.hasLabelFile()) {
			dataWriter.writeAssignedLabelsToFile(data.getAssigns(), ctx.getLabelsFile());
			logger.info("Saving Labels to file");
		}
		if(ctx.hasEvalWorkersFile()) {
			dataWriter.writeTrueWorkerDataToFile(data.getWorkerContResults(), ctx.getEvalWorkersFile());
			logger.info("Saving Workers to file");
		}
		if(ctx.hasEvalobjectsFile()) {
			dataWriter.writeTrueObjectDataToFile(data.getObjects(), ctx.getEvalObjectsFile());
			logger.info("Saving Objects to file");
		}
		if(ctx.hasGoldFile()) {
			dataWriter.writeGoldObjectDataToFile(data.getObjects(), ctx.getGoldFile());
			logger.info("Saving GoldObjects to file");
		}
	}
}
