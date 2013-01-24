package com.datascience.galc;

import org.apache.commons.math3.random.RandomData;
import org.apache.commons.math3.random.RandomDataImpl;

public class Generator {

	public enum Distribution {
		GAUSSIAN, UNIFORM;
	}

	private Distribution	dist;
	private RandomData		randomData;

	public Generator(Distribution d) {

		this.dist = d;
		this.randomData = new RandomDataImpl();
	}

	Double	mu;
	Double	sigma;

	public void setGaussianParameters(Double mu, Double sigma) {

		this.mu = mu;
		this.sigma = sigma;
	}

	Double	up;
	Double	down;

	public void setUniformParameters(Double d, Double u) {

		this.up = u;
		this.down = d;
	}

	public Double nextData() {

		switch (dist) {
			case GAUSSIAN:
				return this.randomData.nextGaussian(mu, sigma);
			case UNIFORM:
				return this.randomData.nextUniform(down, up);
			default:
				return null;
		}

	}

}
