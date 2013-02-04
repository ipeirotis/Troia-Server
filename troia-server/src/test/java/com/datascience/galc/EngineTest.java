package com.datascience.galc;

import org.junit.Before;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.datascience.galc.engine.Engine;
import com.datascience.galc.engine.EngineContext;
import org.junit.Ignore;

/**
 * Just in case: When developing, and you <b>**REALLY**</b> need to run from
 * CLI, use mvn exec:java as shown below:
 *
 * <p>
 * <code>mvn exec:java -Dexec.mainClass=com.andreou.galc.Main -Dexec.args="--input assignedlabels.txt --evalObjects evaluationObjects.txt --evalWorkers evaluationWorkers.txt --correct goldObjects.txt
--synthetic synthetic-options.txt --output results --verbose"</code>
 * </p>
 *
 */
public class EngineTest {
	private EngineContext ctx;
	private CmdLineParser parser;

	@Before
	public void setUp() {
		ctx = new EngineContext();

		parser = new CmdLineParser(ctx);
	}

	private void executeOn(String unparsedArgs) throws Exception {
		parseArgs(unparsedArgs);

		Engine engine = new Engine(ctx);

		engine.execute();
	}

	private void parseArgs(String unparsedArgs) throws CmdLineException {
		String[] args = unparsedArgs.split("\\s+");

		parser.parseArgument(args);
	}

	@Test(expected=CmdLineException.class)
	public void testInvalidOption() throws Exception {
		parseArgs("--foo");
	}

	@Test
	public void testBasicExecution() throws Exception {
		//executeOn("--input data/synthetic/assignedlabels.txt --evalObjects data/synthetic/evaluationObjects.txt --evalWorkers data/synthetic/evaluationWorkers.txt --correct data/synthetic/goldObjects.txt --synthetic data/synthetic/synthetic-options.txt --output results/synthetic");
	}

	@Test
	public void testNewSyntheticData_verbose() throws Exception {
		//executeOn("--input data/synthetic/assignedlabels.txt --evalObjects data/synthetic/evaluationObjects.txt --evalWorkers data/synthetic/evaluationWorkers.txt --correct data/synthetic/goldObjects.txt --synthetic data/synthetic/synthetic-options.txt --output results/synthetic --verbose");
	}
	@Test
	public void testCurrentSyntheticData_verbose() throws Exception {
		//executeOn("--input data/synthetic/assignedlabels.txt --evalObjects data/synthetic/evaluationObjects.txt --evalWorkers data/synthetic/evaluationWorkers.txt --correct data/synthetic/goldObjects.txt --output results/synthetic --verbose");
	}
	@Test
	public void testCurrentSyntheticData_labels_objects_workers() throws Exception {
		//executeOn("--input data/synthetic/assignedlabels.txt --evalObjects data/synthetic/evaluationObjects.txt --evalWorkers data/synthetic/evaluationWorkers.txt --output results/synthetic");
	}

	@Test
	public void testCurrentSyntheticData_labels_objects() throws Exception {
//		executeOn("--input data/synthetic/assignedlabels.txt --evalObjects data/synthetic/evaluationObjects.txt --output results/synthetic");
	}

	@Test
	public void testCurrentSyntheticData_labels() throws Exception {
//		executeOn("--input data/synthetic/assignedlabels.txt --output results/synthetic");
	}

	@Test
	public void testAdcountingData() throws Exception {
//		executeOn("--input data/adcounting/assignedlabels.txt --evalObjects data/adcounting/evaluationObjects.txt --evalWorkers data/adcounting/evaluationWorkers.txt --correct data/adcounting/goldObjects.txt --output results/adcounting");
	}
	@Test
	public void testAdcountingData_labels_objects_workers() throws Exception {
//		executeOn("--input data/adcounting/assignedlabels.txt --evalObjects data/adcounting/evaluationObjects.txt --evalWorkers data/adcounting/evaluationWorkers.txt --output results/adcounting");
	}

	@Test
	public void testAdcountingData_labels_objects() throws Exception {
//		executeOn("--input data/adcounting/assignedlabels.txt --evalObjects data/adcounting/evaluationObjects.txt --output results/adcounting");
	}

	@Ignore
	@Test
	public void testAdcountingData_labels1() throws Exception {
		executeOn("--input data/adcounting/assignedlabels1.txt --output results/adcounting");
	}

	@Ignore
	@Test
	public void testAdcountingData_labels() throws Exception {
		executeOn("--input data/adcounting/assignedlabels.txt --output results/adcounting");
	}
	
	@Ignore
	@Test
	public void testAdcountingData_labelsOld() throws Exception {
		executeOn("--input data/adcounting/assignedlabels.old.txt --output results/adcounting");
	}

}
