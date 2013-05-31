package com.datascience.utils.transformations.simple;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.ContValue;
import com.datascience.core.results.WorkerContResults;
import com.datascience.utils.ITransformation;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.Iterator;

/**
 * @Author: artur
 */
public class WorkerContResultTransform implements ITransformation<WorkerContResults, String> {

	protected Joiner joiner;
	protected Splitter splitter;
	protected CollectionTransform<AssignedLabel<ContValue>> assignedLabelCollectionTransform;

	public WorkerContResultTransform(String separator, CollectionTransform<AssignedLabel<ContValue>> assignedLabelCollectionTransform){
		joiner = Joiner.on(separator).useForNull("null");
		splitter = Splitter.on(separator);
		this.assignedLabelCollectionTransform = assignedLabelCollectionTransform;
	}

	@Override
	public String transform(WorkerContResults result) {
		return joiner.join(new Double[] {
				result.getEst_rho(), result.getEst_mu(), result.getEst_sigma(),
				result.getTrueRho(), result.getTrueMu(), result.getTrueSigma()
		}) + ";" + assignedLabelCollectionTransform.transform(result.getZetaValues());
	}

	@Override
	public WorkerContResults inverse(String object) {
		WorkerContResults ret = new WorkerContResults();
		String[] splitted = object.split(";");
		Iterator<String> values = splitter.split(splitted[0]).iterator();
		ret.setEst_rho(getDoubleValue(values.next()));
		ret.setEst_mu(getDoubleValue(values.next()));
		ret.setEst_sigma(getDoubleValue(values.next()));
		ret.setTrueRho(getDoubleValue(values.next()));
		ret.setTrueMu(getDoubleValue(values.next()));
		ret.setTrueSigma(getDoubleValue(values.next()));
		ret.setZetaValue(assignedLabelCollectionTransform.inverse(splitted[1]));
		return ret;
	}

	private Double getDoubleValue(String s){
		return s == null || s.equals("null") ? null : Double.valueOf(s);
	}
}
