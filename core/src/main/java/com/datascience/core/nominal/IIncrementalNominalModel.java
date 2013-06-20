package com.datascience.core.nominal;

/**
 * User: artur
 * Date: 6/7/13
 */
public interface IIncrementalNominalModel extends INominalModel {
	int getPriorDenominator();
	void setPriorDenominator(int a);
}
