package com.datascience.galc.dataGenerator;

import java.io.IOException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Main {
	public static void main(String[] args) throws IOException {
		EngineContext ctx = new EngineContext();
		CmdLineParser parser = new CmdLineParser(ctx);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println("Usage: \n");
			parser.printUsage(System.err);
			System.err.println();
			return;
		}
		new Engine(ctx).execute();
	}
}