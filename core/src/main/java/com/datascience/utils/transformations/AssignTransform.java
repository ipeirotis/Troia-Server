package com.datascience.utils.transformations;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.utils.ITransformation;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.ArrayList;

/**
* User: artur
* Date: 5/7/13
*/
public class AssignTransform<T> implements ITransformation<AssignedLabel<T>, String> {

	Joiner joiner;
	Splitter splitter;
	ITransformation<T, String> labelTransformation;

	public AssignTransform(String separator, ITransformation<T, String> labelTransformation){
		joiner = Joiner.on(separator);
		splitter = Splitter.on(separator);
		this.labelTransformation = labelTransformation;
	}

	@Override
	public String transform(AssignedLabel<T> object) {
		return joiner.join(new String[]{
				object.getWorker().getName(),
				object.getLobject().getName(),
				labelTransformation.transform(object.getLabel())});
	}

	@Override
	public AssignedLabel<T> inverse(String object) {
		ArrayList<String> items = Lists.newArrayList(splitter.split(object));
		Worker worker = new Worker(items.get(0));
		LObject<T> obj = new LObject<T>(items.get(1));
		return new AssignedLabel<T>(worker, obj, labelTransformation.inverse(joiner.join(items.subList(2, items.size()))));
	}
}
