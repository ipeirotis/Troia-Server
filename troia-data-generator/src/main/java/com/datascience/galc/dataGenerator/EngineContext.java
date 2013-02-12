package com.datascience.galc.dataGenerator;


import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.kohsuke.args4j.Option;

public class EngineContext {

	@Option(
		name = "--labels",
		metaVar = "<labelsfile>",
		usage = "A filename for labels.")
	private String labelsfile = "";

	@Option(
		name = "--gold",
		metaVar = "<goldfile>",
		usage = "A filename for gold objects.")
	private String goldFile = "";

	@Option(
			name = "--evalObjects",
			metaVar = "<evalobjectsfile>",
			usage = "A filename for eval objects")
		private String evalobjectsfile = "";

		@Option(
			name = "--evalWorkers",
			metaVar = "<evalworkersfile>",
			usage = "A filename for eval workers")
		private String evalworkersfile = "";
	
	@Option(
		name = "--synthetic",
		required = true,
		metaVar = "<syntheticoptionsfile>",
		usage = "A tab-separated text file. Each line has the form <attribute><tab><value> and records the options for the creation of new synthetic data.")
	private String syntheticoptionsfile = "";

	@Option(
		name = "--verbose",
		metaVar = "<verbose>",
		usage = "Verbose Mode?")
	private boolean verbose;

	
	public boolean hasLabelFile() {
		return isNotBlank(labelsfile);
	}

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
		return isNotBlank(syntheticoptionsfile);
	}

	public String getSyntheticOptionsFile() {
		return syntheticoptionsfile;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean b) {
		this.verbose = b;
	}

}
