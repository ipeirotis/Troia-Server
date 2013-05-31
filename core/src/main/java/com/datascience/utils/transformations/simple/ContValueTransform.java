package com.datascience.utils.transformations.simple;

import com.datascience.core.base.ContValue;
import com.datascience.utils.ITransformation;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.Iterator;

/**
* User: artur
* Date: 5/7/13
*/
public class ContValueTransform implements ITransformation<ContValue, String> {

	Joiner joiner;
	Splitter splitter;
	String separator;

	public ContValueTransform(String separator){
		joiner = Joiner.on(separator);
		splitter = Splitter.on(separator);
		this.separator = separator;
	}

	@Override
	public String transform(ContValue s) {
		if (s != null)
			return joiner.join(s.getValue().toString(), s.getZeta() != null ? s.getZeta().toString() : "");
		else
			return separator;
	}

	@Override
	public ContValue inverse(String object) {
		Iterator<String> values = splitter.split(object).iterator();
		String value = values.next();
		String zeta = values.next();
		if (value.isEmpty() && zeta.isEmpty())
			return null;
		return new ContValue(Double.parseDouble(value), zeta.isEmpty() ? null : Double.parseDouble(zeta));
	}
}
