package com.datascience.utils.transformations;

import com.datascience.core.results.DatumResult;
import com.datascience.utils.ITransformation;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

/**
 * @Author: artur
 */
public class DatumResultTransform implements ITransformation<DatumResult, String> {

	protected Joiner joiner;
	protected Splitter splitter;
	protected MapTransform mapTransform;

	public DatumResultTransform(String separator){
		joiner = Joiner.on(separator);
		splitter = Splitter.on(separator);
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
