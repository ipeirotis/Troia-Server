package com.datascience.utils.transformations;

import com.datascience.core.base.CategoryPair;
import com.datascience.core.results.ResultsFactory;
import com.datascience.core.results.WorkerResult;
import com.datascience.core.stats.MultinomialConfusionMatrix;
import com.datascience.utils.ITransformation;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @Author: artur
 */
public class WorkerResultTransform implements ITransformation<WorkerResult, String> {

	protected Joiner joiner;
	protected Splitter splitter;
	protected ResultsFactory.WorkerResultNominalFactory resultFactory;

	public WorkerResultTransform(ResultsFactory.WorkerResultNominalFactory wrnf){
		joiner = Joiner.on(";");
		splitter = Splitter.on(";");
		resultFactory = wrnf;
	}

	/*
	c1,c2,0.1;c1,c1,0.9;c2,c1,0.1;c2,c2,0.9|c1:2.0;c2:3.0
	 */
	@Override
	public String transform(WorkerResult result) {
		MultinomialConfusionMatrix cm = (MultinomialConfusionMatrix) result.getConfusionMatrix();
		LinkedList<String> stringItems = new LinkedList<String>();
		for (Map.Entry<CategoryPair, Double> item : cm.getMatrix().entrySet()){
			stringItems.add(item.getKey().from + "," + item.getKey().to + "," + item.getValue());
		}
		return joiner.join(stringItems);
	}

	@Override
	public WorkerResult inverse(String object) {
		WorkerResult ret = resultFactory.create();
		MultinomialConfusionMatrix cm = (MultinomialConfusionMatrix) ret.getConfusionMatrix();
		cm.empty();
		Map<String, Double> map = new HashMap<String, Double>();
		for (String item : splitter.split(object)){
			String[] matrixVal = item.split(",");
			cm.addError(matrixVal[0], matrixVal[1], Double.parseDouble(matrixVal[2]));
		}
		return ret;
	}
}
