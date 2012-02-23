package com.ipeirotis.utils;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

public class DawidSkeneExperimenter {
    private static Options cli_options;

    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(DawidSkeneExperimenter.class.toString(),
                cli_options);
    }
}
