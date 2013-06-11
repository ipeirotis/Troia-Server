package com.datascience.datastoring.datamodels.memory;

import com.datascience.core.nominal.IIncrementalNominalModel;

/**
 * User: artur
 */
public class IncrementalNominalModel extends NominalModel implements IIncrementalNominalModel {

	private int priorDenominator;

	@Override
	public int getPriorDenominator(){
		return priorDenominator;
	}

	@Override
	public void setPriorDenominator(int a){
		priorDenominator = a;
	}
}

