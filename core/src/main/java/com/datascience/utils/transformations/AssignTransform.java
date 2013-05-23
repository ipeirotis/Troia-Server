package com.datascience.utils.transformations;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.utils.ITransformation;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.util.Iterator;

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
		return joiner.join(
				object.getWorker().getName(),
				object.getLobject().getName(),
				labelTransformation.transform(object.getLabel()));
	}

	@Override
	public AssignedLabel<T> inverse(String object) {
		Iterator<String> values = splitter.split(object).iterator();
		Worker worker = new Worker(values.next());
		LObject<T> obj = new LObject<T>(values.next());
		return new AssignedLabel<T>(worker, obj, labelTransformation.inverse(joiner.join(values)));
	}
}
