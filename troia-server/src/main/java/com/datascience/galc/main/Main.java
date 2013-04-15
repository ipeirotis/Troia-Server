package com.datascience.galc.main;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class Main {
	public static void main(String[] args) {
		EngineContext ctx = new EngineContext();

		CmdLineParser parser = new CmdLineParser(ctx);

		try {
			parser.parseArgument(args);

		} catch (CmdLineException e) {
			System.err.println(e);

			showUsage(parser);

			return;
		}

		Engine engine = new Engine(ctx);

		engine.execute();
	}

	private static void showUsage(CmdLineParser parser) {
		System.err.println("Usage: \n");
		parser.printUsage(System.err);
		System.err.println();
	}

}
