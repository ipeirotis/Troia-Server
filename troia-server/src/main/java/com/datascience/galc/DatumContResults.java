package com.datascience.galc;

import com.datascience.core.base.ContValue;
import com.datascience.core.base.LObject;

public class DatumContResults {

	private Double							est_value;
	private Double							est_zeta;

	private Double 							distributionMu;
	private Double 							distributionSigma;

	protected LObject<ContValue> object;
	
	public DatumContResults(LObject<ContValue> object) {
		this.object = object;
	}

	public LObject<ContValue> getObject(){
		return object;
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
