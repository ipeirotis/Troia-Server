package com.datascience.galc.dataGenerator;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import com.datascience.galc.dataGenerator.engine.Engine;
import com.datascience.galc.dataGenerator.engine.EngineContext;

public class SyntheticDataGeneratorTest {

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

	
	@Ignore
	@Test
	public void testGeneratedSyntheticData() {
		final SyntheticDataGenerator dataGenerator = new SyntheticDataGenerator();
	}
	
	@Ignore
	@Test
	public void testBasicExecution() throws Exception {
		executeOn(" --labels data/synthetic/assignedlabels.txt --gold data/synthetic/goldObjects.txt --synthetic data/synthetic/synthetic-options.txt --output results/synthetic");
	}

}