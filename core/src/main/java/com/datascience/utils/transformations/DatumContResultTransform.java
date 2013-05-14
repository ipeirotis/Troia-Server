package com.datascience.utils.transformations;

import com.datascience.core.results.DatumContResults;
import com.datascience.utils.ITransformation;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.ArrayList;

/**
 * @Author: artur
 */
public class DatumContResultTransform implements ITransformation<DatumContResults, String> {

	protected Joiner joiner;
	protected Splitter splitter;

	public DatumContResultTransform(String separator){
		joiner = Joiner.on(separator).useForNull("null");
		splitter = Splitter.on(separator);
	}

	@Override
	public String transform(DatumContResults result) {
		return joiner.join(new Double[] {
			result.getEst_value(), result.getEst_zeta(), result.getDistributionMu(), result.getDistributionSigma()
		});
	}

	@Override
	public DatumContResults inverse(String object) {
		DatumContResults ret = new DatumContResults();
		ArrayList<String> items = Lists.newArrayList(splitter.split(object));
		ret.setEst_value(getDoubleValue(items.get(0)));
		ret.setEst_zeta(getDoubleValue(items.get(1)));
		ret.setDistributionMu(getDoubleValue(items.get(2)));
		ret.setDistributionSigma(getDoubleValue(items.get(3)));
		return ret;
	}

	private Double getDoubleValue(String s){
		return s == null || s.equals("null") ? null : Double.valueOf(s);
	}
}
