package com.datascience.utils.transformations;

import com.datascience.core.results.DatumContResults;
import com.datascience.utils.ITransformation;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.Iterator;

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
		return joiner.join(result.getEst_value(), result.getEst_zeta(), result.getDistributionMu(), result.getDistributionSigma());
	}

	@Override
	public DatumContResults inverse(String object) {
		DatumContResults ret = new DatumContResults();
		Iterator<String> values = splitter.split(object).iterator();
		ret.setEst_value(getDoubleValue(values.next()));
		ret.setEst_zeta(getDoubleValue(values.next()));
		ret.setDistributionMu(getDoubleValue(values.next()));
		ret.setDistributionSigma(getDoubleValue(values.next()));
		return ret;
	}

	private Double getDoubleValue(String s){
		return s == null || s.equals("null") ? null : Double.valueOf(s);
	}
}
