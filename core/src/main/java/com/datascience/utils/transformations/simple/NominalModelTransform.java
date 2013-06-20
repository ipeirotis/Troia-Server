package com.datascience.utils.transformations.simple;

import com.datascience.datastoring.datamodels.memory.NominalModel;
import com.datascience.utils.ITransformation;

/**
* User: artur
* Date: 5/7/13
*/
public class NominalModelTransform implements ITransformation<NominalModel, String> {

	MapTransform mapTransform;

	public NominalModelTransform(String mapSeparator){
		mapTransform = new MapTransform(mapSeparator);
	}

	@Override
	public String transform(NominalModel object) {
		return mapTransform.transform(object.getCategoryPriors());
	}

	@Override
	public NominalModel inverse(String object) {
		NominalModel model = new NominalModel();
		model.setCategoryPriors(mapTransform.inverse(object));
		return model;
	}
}
