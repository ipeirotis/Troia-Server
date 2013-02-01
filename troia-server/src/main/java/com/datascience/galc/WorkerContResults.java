package com.datascience.galc;

import java.util.HashSet;
import java.util.Set;

public class WorkerContResults {
	private Double							est_rho;
	private Double							est_mu;
	private Double							est_sigma;

	// The labels normalized into empirical z-values (subtracted empirical mean, divided by stdev)
	private Set<AssignedLabel>	zeta = new HashSet<AssignedLabel>();

	// True values
	private Double							true_mu;
	private Double							true_sigma;
	private Double							true_rho;

	public WorkerContResults() {
		
	}
	
	public Set<AssignedLabel> getZetaValues() {

		return zeta;
	}

	public Double getZeta(Double label) {

		return (label - this.est_mu) / this.est_sigma;
	}

	public Double getBeta() {
		Double t1 = Math.pow(est_rho, 2);
		Double t = 1 - t1;
		if (t == 0.0)
			System.err.print("woops " + "rho^2:" + t1 + ", w.est_rho:" + est_rho + " ");

		return 1. / t;
	}

	public Double getTrueMu() {

		return true_mu;
	}

	public void setTrueMu(Double mu) {

		this.true_mu = mu;
	}

	public Double getTrueSigma() {

		return true_sigma;
	}

	public void setTrueSigma(Double sigma) {

		this.true_sigma = sigma;
	}

	public Double getTrueRho() {

		return true_rho;
	}

	public void setTrueRho(Double rho) {

		this.true_rho = rho;
	}

/*************************************************************/
	/**
	 * @return the est_rho
	 */
	public Double getEst_rho() {

		return est_rho;
	}

	/**
	 * @param est_rho the est_rho to set
	 */
	public void setEst_rho(Double est_rho) {

		if (est_rho > 0.9999)
			this.est_rho = 0.9999;
		else if (est_rho < -0.9999)
			this.est_rho = -0.9999;
		else
			this.est_rho = est_rho;
	}

	/**
	 * @return the est_mu
	 */
	public Double getEst_mu() {

		return est_mu;
	}

	/**
	 * @param est_mu the est_mu to set
	 */
	public void setEst_mu(Double est_mu) {

		this.est_mu = est_mu;
	}

	/**
	 * @return the est_sigma
	 */
	public Double getEst_sigma() {

		return est_sigma;
	}

	/**
	 * @param est_sigma the est_sigma to set
	 */
	public void setEst_sigma(Double est_sigma) {

		//double epsilon = 0.0001;
		//if (est_sigma<epsilon) est_sigma = epsilon;
		this.est_sigma = est_sigma;
	}

	public Double estimatedAbsZetaError() {
		return Math.sqrt(1 - Math.pow(this.est_rho,2)) * Math.sqrt(2) / Math.sqrt(Math.PI);
	}

	public static Double estimatedAbsZetaError(Double rho) {

		return Math.sqrt(1 - Math.pow(rho,2)) * Math.sqrt(2) / Math.sqrt(Math.PI);
	}

}
