package com.datascience.galc.main;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import org.kohsuke.args4j.Option;

public class EngineContext {

	@Option(
		name = "--input",
		metaVar = "<inputfile>",
		required = true,
		usage = "A tab-separated text file. Each line has the form <workerid><tab><objectid><tab><assigned_label> and records the label that the given worker gave to that object")
	private String inputfile = "";

	@Option(
		name = "--evalObjects",
		metaVar = "<trueobjectsfile>",
		usage = "")
	private String trueobjectsfile = "";

	@Option(
		name = "--evalWorkers",
		metaVar = "<trueworkersfile>",
		usage = "")
	private String trueworkersfile = "";

	@Option(
		name = "--correct",
		metaVar = "<correctfile>",
		usage = "A tab-separated text file. Each line has the form <objectid><tab><assigned_label> and records the correct labels for whatever objects we have them.")
	private String correctFile = "";

	@Option(
		name = "--output",
		metaVar = "<outputfolder>",
		usage = "An Evaluation Report File")
	private String outputfolder = "results";

	public String getInputFile() {
		return inputfile;
	}

	public void setInputFile(String inputfile) {
		this.inputfile = inputfile;
	}

	public boolean hasTrueObjectsFile() {
		return isNotBlank(trueobjectsfile);
	}
	public String getTrueObjectsFile() {
		return trueobjectsfile;
	}

	public void setTrueObjectsFile(String trueobjectsfile) {
		this.trueobjectsfile = trueobjectsfile;
	}

	public boolean hasTrueWorkersFile() {
		return isNotBlank(trueworkersfile);
	}

	public String getTrueWorkersFile() {
		return trueworkersfile;
	}

	public void setTrueWorkersFile(String trueworkersfile) {
		this.trueworkersfile = trueworkersfile;
	}

	public boolean hasCorrectFile() {
		return isNotBlank(correctFile);
	}

	public String getCorrectFile() {
		return correctFile;
	}

	public void setCorrectFile(String correctfile) {
		this.correctFile = correctfile;
	}

	public String getOutputFolder() {
		return outputfolder;
	}

	public void setOutputFolder(String outputfolder) {
		this.outputfolder = outputfolder;
	}
}
