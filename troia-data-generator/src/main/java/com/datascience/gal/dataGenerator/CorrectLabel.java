package com.datascience.gal.dataGenerator;


public class CorrectLabel {

	private String objectName;
	private String correctLabel;

	public CorrectLabel(String objectName, String correctLabel) {
		this.objectName = objectName;
		this.correctLabel = correctLabel;
	}

	public String getObjectName() {
		return objectName;
	}

	public String getCorrectLabel() {
		return correctLabel;
	}

}
