package com.datascience.galc;

import java.util.Set;
import java.util.HashSet;
import com.google.common.base.Objects;

public class Worker {

	private String							name;

	private Double							est_rho;
	private Double							est_mu;
	private Double							est_sigma;

	// The labels assigned by the worker
	private Set<AssignedLabel>	labels;
	// The labels normalized into empirical z-values (subtracted empirical mean, divided by stdev)
	private Set<AssignedLabel>	zeta;

	// True values
	private Double							true_mu;
	private Double							true_sigma;
	private Double							true_rho;

	public Worker(String name) {

		this.name = name;
		this.labels = new HashSet<AssignedLabel>();
	}

	public void addAssignedLabel(AssignedLabel al) {

		if (al.getWorkerName().equals(name)) {
			this.labels.add(al);
		}
	}

	public Set<AssignedLabel> getZetaValues() {

		return zeta;
	}

	public void computeZetaValues() {

		int n = labels.size();
		double mu_worker = 0.0;
		double mu_square = 0.0;
		for (AssignedLabel al : labels) {
			mu_worker += al.getLabel();
			mu_square += Math.pow(al.getLabel(), 2);
		}

		this.est_mu = mu_worker / n;
		this.est_sigma = Math.sqrt((1.0 / n) * (mu_square - Math.pow(mu_worker, 2) / n));
		//System.out.println(this.toString());
		if(this.est_sigma==0.0) {
			this.est_sigma = 0.00000000001;
			System.out.println("[Single Label Worker: " +this.name+"]");
		}

		this.zeta = new HashSet<AssignedLabel>();
		for (AssignedLabel al : labels) {
			Double z = (al.getLabel() - this.est_mu) / this.est_sigma;
			AssignedLabel zl = new AssignedLabel(al.getWorkerName(), al.getObjectName(), z);
			this.zeta.add(zl);
		}
	}

	public Double getZeta(Double label) {

		return (label - this.est_mu) / this.est_sigma;
	}

	public String getName() {

		return name;
	}

	public void setName(String name) {

		this.name = name;
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

	@Override
	public int hashCode() {
		return Objects.hashCode( this.name);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Worker))
			return false;
		Worker other = (Worker) obj;
		return Objects.equal(this.name, other.name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return Objects.toStringHelper(this)
			       .add("name", name)
			       .add("est_rho", est_rho)
			       .add("true_rho", true_rho)
			       .add("est_mu", est_mu)
			       .add("true_mu", true_mu)
			       .add("est_sigma", est_sigma)
			       .add("true_sigma", true_sigma)
			       .toString();
	}

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

	/**
	 * @return the labels
	 */
	public Set<AssignedLabel> getLabels() {

		return labels;
	}

}
