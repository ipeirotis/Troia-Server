package com.datascience.core.results;

import com.google.common.base.Objects;

public class DatumContResults {
	
	private Double							est_value;
	private Double							est_zeta;

	private Double 							distributionMu;
	private Double 							distributionSigma;

	public DatumContResults() {
	}

	public Double getEst_value() {
		return est_value;
	}

	public void computeEstValue(){
		this.est_value=  this.est_zeta * this.distributionSigma + this.distributionMu;
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

	@Override
	public boolean equals(Object other){
		if (other instanceof DatumContResults) {
			DatumContResults obj = (DatumContResults) other;
			return Objects.equal(est_value, obj.est_value) &&
					Objects.equal(est_zeta, obj.est_zeta) &&
					Objects.equal(distributionMu, obj.distributionMu) &&
					Objects.equal(distributionSigma, obj.distributionSigma);
		}
		return false;
	}
}
