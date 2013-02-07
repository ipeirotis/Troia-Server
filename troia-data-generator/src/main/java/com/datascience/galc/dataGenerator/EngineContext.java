package com.datascience.galc.dataGenerator;


import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.kohsuke.args4j.Option;

public class EngineContext {

	@Option(
		name = "--labels",
		metaVar = "<labelsfile>",
		required = true,
		usage = "A tab-separated text file. Each line has the form <workerid><tab><objectid><tab><assigned_label> and records the label that the given worker gave to that object")
	private String labelsfile = "";

	@Option(
		name = "--gold",
		metaVar = "<goldfile>",
		usage = "A tab-separated text file. Each line has the form <objectid><tab><assigned_label> and records the correct labels for whatever objects we have them.")
	private String goldFile = "";

	@Option(
			name = "--evalObjects",
			metaVar = "<evalobjectsfile>",
			usage = "")
		private String evalobjectsfile = "";

		@Option(
			name = "--evalWorkers",
			metaVar = "<evalworkersfile>",
			usage = "")
		private String evalworkersfile = "";
	
	@Option(
		name = "--synthetic",
		metaVar = "<syntheticoptionsfile>",
		usage = "A tab-separated text file. Each line has the form <attribute><tab><value> and records the options for the creation of new synthetic data.")
	private String syntheticOptionsFile = "";

	@Option(
		name = "--output",
		metaVar = "<outputfolder>",
		usage = "An Evaluation Report File")
	private String outputfolder = "results";

	@Option(
		name = "--verbose",
		metaVar = "<verbose>",
		usage = "Verbose Mode?")
	private boolean verbose;

	
	public String getLabelsFile() {
		return labelsfile;
	}

	public void setLabelsFile(String s) {
		this.labelsfile = s;
	}

	public boolean hasGoldFile() {
		return isNotBlank(goldFile);
	}

	public String getGoldFile() {
		return goldFile;
	}

	public void setGoldFile(String s) {
		this.goldFile = s;
	}
	
	public boolean hasEvalobjectsFile() {
		return isNotBlank(evalobjectsfile);
	}
	
	public String getEvalObjectsFile() {
		return evalobjectsfile;
	}

	public boolean hasEvalWorkersFile() {
		return isNotBlank(evalworkersfile);
	}
	
	public String getEvalWorkersFile() {
		return evalworkersfile;
	}

	public boolean isSyntheticDataSet() {
		return isNotBlank(syntheticOptionsFile);
	}

	public String getSyntheticOptionsFile() {
		return syntheticOptionsFile;
	}

	public String getOutputFolder() {
		return outputfolder;
	}

	public void setOutputFolder(String s) {
		this.outputfolder = s;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean b) {
		this.verbose = b;
	}

}
