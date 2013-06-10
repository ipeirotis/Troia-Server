package com.datascience.utils.transformations.simple;

import com.datascience.datastoring.datamodels.memory.IncrementalNominalModel;
import com.datascience.datastoring.datamodels.memory.NominalModel;
import com.datascience.utils.ITransformation;

/**
* User: artur
* Date: 5/7/13
*/
public class IncrementalNominalModelTransform implements ITransformation<IncrementalNominalModel, String> {

	NominalModelTransform nmt;

	public IncrementalNominalModelTransform(String mapSeparator){
		nmt = new NominalModelTransform(mapSeparator);
	}

	@Override
	public String transform(IncrementalNominalModel object) {
		return nmt.transform(object) + "|" + object.getPriorDenominator();
	}

	@Override
	public IncrementalNominalModel inverse(String object) {
		String[] s = object.split("\\|");
		NominalModel nm = nmt.inverse(s[0]);
		IncrementalNominalModel model = new IncrementalNominalModel();
		model.setPriorDenominator(Integer.valueOf(s[1]));
		model.setCategoryPriors(nm.getCategoryPriors());
		return model;
	}
}
