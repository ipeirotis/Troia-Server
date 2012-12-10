package com.datascience.gal.dataGenerator;


public class ArtificialWorker {





	/**
	 * @return Worker name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name Worker name
	 */
	public void setName(String name) {
		this.name = name;
	}


	/**
	 * @return the confusionMatrix
	 */
	public ConfusionMatrix getConfusionMatrix() {
		return confusionMatrix;
	}

	/**
	 * @param confusionMatrix the confusionMatrix to set
	 */
	public void setConfusionMatrix(ConfusionMatrix confusionMatrix) {
		this.confusionMatrix = confusionMatrix;
	}


	/**
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ArtificialWorker [name=" + name + ", confusionMatrix="
			   + confusionMatrix + "]";
	}

	/**
	 * @see java.lang.Object#equals()
	 */
	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof ArtificialWorker)) {
			return false;
		}
		ArtificialWorker a = (ArtificialWorker) o;
		return confusionMatrix.equals(a.confusionMatrix) &&
			   name.equals(a.name);
	}

	/**
	 * Worker name
	 */
	private String name;

	/**
	 * Worker confusion matrix.
	 * Confusion matrix is used to indicate what
	 */
	private ConfusionMatrix confusionMatrix;
}
