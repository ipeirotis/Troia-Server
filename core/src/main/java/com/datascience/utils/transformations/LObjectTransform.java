package com.datascience.utils.transformations;

import com.datascience.core.base.LObject;
import com.datascience.utils.ITransformation;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.ArrayList;

/**
* User: artur
* Date: 5/7/13
*/
public class LObjectTransform<T> implements ITransformation<LObject<T>, String> {

	Joiner joiner;
	Splitter splitter;
	ITransformation<T, String> labelTransformation;

	public LObjectTransform(String separator, ITransformation<T, String> labelTransformation){
		joiner = Joiner.on(separator);
		splitter = Splitter.on(separator);
		this.labelTransformation = labelTransformation;
	}

	@Override
	public String transform(LObject<T> object) {
		return joiner.join(
				object.getName(),
				labelTransformation.transform(object.getGoldLabel()),
				labelTransformation.transform(object.getEvaluationLabel()));
	}

	@Override
	public LObject<T> inverse(String object) {
		ArrayList<String> items = Lists.newArrayList(splitter.split(object));
		LObject<T> obj = new LObject<T>(items.get(0));
		int half = (items.size()-1)/2;
		obj.setGoldLabel(labelTransformation.inverse(joiner.join(items.subList(1, 1 + half))));
		obj.setEvaluationLabel(labelTransformation.inverse(joiner.join(items.subList(1 + half, items.size()))));
		return obj;
	}
}
