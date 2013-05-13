package com.datascience.core.results;

public class DatumContResults {
	
	private Double							est_value;
	private Double							est_zeta;

	private Double 							distributionMu;
	private Double 							distributionSigma;

	public DatumContResults() {
	}

	public Double getEst_value() {
		this.est_value=  this.est_zeta * this.distributionSigma + this.distributionMu;
		return est_value;
	}

	public void setEst_value(Double est_value) {
		this.est_value = est_value;
	}

	public Double getEst_zeta() {
		return est_zeta;
	}

	public void setEst_zeta(Double est_zeta) {
		this.est_zeta = est_zeta;
	}

	public Double getDistributionMu() {
		return distributionMu;
	}

	public void setDistributionMu(Double distributionMu) {
		this.distributionMu = distributionMu;
	}

	public Double getDistributionSigma() {
		return distributionSigma;
	}

	public void setDistributionSigma(Double distributionSigma) {
		this.distributionSigma = distributionSigma;
	}	
}
