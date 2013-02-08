package com.datascience.galc.dataGenerator;

import java.io.IOException;
import org.junit.Test;


public class SyntheticDataGeneratorTest {

	@Test
	public void testGenerationSyntheticData() {
		new SyntheticDataGenerator().generate();
	}
	
	@Test
	public void testWriteSyntheticData() throws IOException {
		final SyntheticData data = new SyntheticDataGenerator().generate();
		final SyntheticDataWriter dataWriter = new SyntheticDataWriter(data);
		dataWriter.writeAssignedLabelsToFile(data.getAssigns(), "target/assignedLabels.txt");
		dataWriter.writeTrueWorkerDataToFile(data.getWorkerContResults(), "target/evaluatedWorkers.txt");
		dataWriter.writeTrueObjectDataToFile(data.getObjects(), "target/evaluatedObjects.txt");
		dataWriter.writeGoldObjectDataToFile(data.getObjects(), "target/goldObjects.txt");
	}

}