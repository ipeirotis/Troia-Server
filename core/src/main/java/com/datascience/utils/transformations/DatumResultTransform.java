package com.datascience.utils.transformations;

import com.datascience.core.results.DatumResult;
import com.datascience.utils.ITransformation;

/**
 * @Author: artur
 */
public class DatumResultTransform implements ITransformation<DatumResult, String> {

	protected MapTransform mapTransform;

	public DatumResultTransform(String separator){
		mapTransform = new MapTransform(";");
	}

	@Override
	public String transform(DatumResult result) {
		return mapTransform.transform(result.getCategoryProbabilites());
	}

	@Override
	public DatumResult inverse(String object) {
		DatumResult ret = new DatumResult();
		ret.setCategoryProbabilites(mapTransform.inverse(object));
		return ret;
	}
}
