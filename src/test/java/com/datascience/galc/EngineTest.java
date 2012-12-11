package com.datascience.galc;

import org.junit.Before;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.datascience.galc.engine.Engine;
import com.datascience.galc.engine.EngineContext;

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
		executeOn("--input galc-data/assignedlabels.txt --evalObjects galc-data/evaluationObjects.txt --evalWorkers galc-data/evaluationWorkers.txt --correct galc-data/goldObjects.txt --synthetic galc-config/synthetic-options.txt --output galc-results");
	}

	@Test
	public void testAnotherScenario() throws Exception {
		executeOn("--input galc-data/assignedlabels.txt --evalObjects galc-data/evaluationObjects.txt --evalWorkers galc-data/evaluationWorkers.txt --correct galc-data/goldObjects.txt --synthetic galc-config/synthetic-options.txt --output galc-results --verbose");
		
	}
}
