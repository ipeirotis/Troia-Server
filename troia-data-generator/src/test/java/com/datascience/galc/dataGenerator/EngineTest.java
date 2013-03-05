package com.datascience.galc.dataGenerator;

import org.junit.Before;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class EngineTest {
	private EngineContext ctx;
	private CmdLineParser parser;

	@Before
	public void setUp() {
		ctx = new EngineContext();
		parser = new CmdLineParser(ctx);
		parser.setUsageWidth(200);
	}

	private void executeOn(String unparsedArgs) throws Exception {
     try {
    	 parseArgs(unparsedArgs);

     } catch( CmdLineException e ) {
            System.err.println(e.getMessage());
            parser.printUsage(System.err);
        }
	    Engine engine = new Engine(ctx);
		engine.execute();
	}

	private void parseArgs(String unparsedArgs) throws CmdLineException {
		String[] args = unparsedArgs.split("\\s+");
		parser.parseArgument(args);
	}


	/*
	this test do nothing except checking if no exceptions is being thrown by data-generator
	 */
	@Test
	public void testBasicExecution() throws Exception {
		executeOn("--labels data/synthetic/assignedlabels.txt --evalObjects data/synthetic/evaluationObjects.txt --evalWorkers data/synthetic/evaluationWorkers.txt --gold data/synthetic/goldObjects.txt --synthetic data/synthetic/synthetic-options.txt");
	}

}
