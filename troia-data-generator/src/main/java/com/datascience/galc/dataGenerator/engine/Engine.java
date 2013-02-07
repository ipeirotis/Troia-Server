package com.datascience.galc.dataGenerator.engine;

import com.datascience.galc.dataGenerator.SyntheticData;
import com.datascience.galc.dataGenerator.SyntheticDataGenerator;
import com.datascience.galc.dataGenerator.SyntheticDataWriter;
import java.io.IOException;

public class Engine {
	
	private EngineContext ctx;
	
	public Engine(EngineContext ctx) {
		this.ctx = ctx;
	}
	
	public void execute() throws IOException {

		SyntheticDataGenerator dataGenerator = new SyntheticDataGenerator(ctx.getSyntheticOptionsFile());
		SyntheticData data = dataGenerator.generate();
		SyntheticDataWriter dataWriter = new SyntheticDataWriter((data));
		dataWriter.writeAssignedLabelsToFile(data.getAssigns(), ctx.getLabelsFile());
		if(ctx.hasEvalWorkersFile()) {
			dataWriter.writeTrueWorkerDataToFile(data.getWorkerContResults(), ctx.getEvalWorkersFile());
		}
		if(ctx.hasEvalobjectsFile()) {
			dataWriter.writeTrueObjectDataToFile(data.getObjects(), ctx.getEvalObjectsFile());
		}
		if(ctx.hasGoldFile()) {
			dataWriter.writeGoldObjectDataToFile(data.getGoldObjects(), ctx.getGoldFile());
		}
	}
}
