package com.datascience.galc;

public class DatumContResults {

	private Boolean							isGold;
	private Double							goldValue;
	private Double							goldZeta;

	private Double							est_value;
	private Double							est_zeta;

	// Data generation characteristics
	private Double							trueValue;
	private Double							trueZeta;
	
	private Double 							distributionMu;
	private Double 							distributionSigma;
	
	public DatumContResults() {
		
		this.isGold = false;

	}

	public Boolean isGold() {

		return isGold;
	}

	public void setGold(Boolean isGold) {

		this.isGold = isGold;
	}

	public Double getGoldValue() {

		return goldValue;
	}

	public void setGoldValue(Double goldValue) {

		this.goldValue = goldValue;
	}

	public Double getGoldZeta() {

		return goldZeta;
	}

	public void setGoldZeta(Double goldZeta) {

		this.goldZeta = goldZeta;
	}

	/**
	 * @return the est_value
	 */
	public Double getEst_value() {

		this.est_value=  this.est_zeta * this.distributionSigma + this.distributionMu;
		
		return est_value;
	}

	/**
	 * @param est_value the est_value to set
	 */
	public void setEst_value(Double est_value) {

		this.est_value = est_value;
	}

	/**
	 * @return the est_zeta
	 */
	public Double getEst_zeta() {

		return est_zeta;
	}

	/**
	 * @param est_zeta the est_zeta to set
	 */
	public void setEst_zeta(Double est_zeta) {

		this.est_zeta = est_zeta;
	}

	/**
	 * @return the trueZeta
	 */
	public Double getTrueZeta() {

		return trueZeta;
	}

	/**
	 * @param trueZeta the trueZeta to set
	 */
	public void setTrueZeta(Double trueZeta) {

		this.trueZeta = trueZeta;
	}

	public Double getTrueValue() {

		return trueValue;
	}

	public void setTrueValue(Double trueValue) {

		this.trueValue = trueValue;
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
