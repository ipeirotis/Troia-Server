package com.datascience.core.results;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;

public class WorkerContResults {

	private static Logger logger = Logger.getLogger(WorkerContResults.class);

	private Double est_rho;
	private Double est_mu;
	private Double est_sigma;

	// The labels normalized into empirical z-values (subtracted empirical mean, divided by stdev)
	private Set<AssignedLabel<ContValue>> zeta;

	// True values
	private Double true_mu;
	private Double true_sigma;
	private Double true_rho;

	public WorkerContResults() {
		zeta = new HashSet<AssignedLabel<ContValue>>();
	}

	public Set<AssignedLabel<ContValue>> getZetaValues() {
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


	public void computeZetaValues(Collection<AssignedLabel<ContValue>> workersAssigns) {
		int n = workersAssigns.size();
		double mu_worker = 0.0;
		double mu_square = 0.0;
		for (AssignedLabel<ContValue> al : workersAssigns) {
			double label = al.getLabel().getValue();
			mu_worker += label;
			mu_square += Math.pow(label, 2);
		}

		setEst_mu(mu_worker / n);
		setEst_sigma(Math.sqrt((1.0 / n) * (mu_square - Math.pow(mu_worker, 2) / n)));
		//logger.info(this.toString());
		if(getEst_sigma()==0.0) {
			setEst_sigma(0.00000000001);
		}

		for (AssignedLabel<ContValue> al : workersAssigns) {
			double label = al.getLabel().getValue();
			Double z = (label - getEst_mu()) / getEst_sigma();
			AssignedLabel zl = new AssignedLabel<ContValue>(al.getWorker(), al.getLobject(), new ContValue(z));
			getZetaValues().add(zl);
		}
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

	public Double getEst_rho() {

		return est_rho;
	}

	public void setEst_rho(Double est_rho) {

		if (est_rho > 0.9999)
			this.est_rho = 0.9999;
		else if (est_rho < -0.9999)
			this.est_rho = -0.9999;
		else
			this.est_rho = est_rho;
	}

	public Double getEst_mu() {

		return est_mu;
	}

	public void setEst_mu(Double est_mu) {

		this.est_mu = est_mu;
	}

	public Double getEst_sigma() {

		return est_sigma;
	}

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
