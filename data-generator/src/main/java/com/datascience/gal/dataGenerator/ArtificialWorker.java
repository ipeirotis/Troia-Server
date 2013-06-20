package com.datascience.gal.dataGenerator;


import com.datascience.core.base.Worker;
import com.google.common.base.Objects;

public class ArtificialWorker extends Worker {

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

	@Override
	public int hashCode(){
		return Objects.hashCode(name);
	}


	/**
	 * Worker confusion matrix.
	 * Confusion matrix is used to indicate what
	 */
	private ConfusionMatrix confusionMatrix;
}
