package com.datascience.utils.transformations;

import com.datascience.utils.ITransformation;

/**
* User: artur
* Date: 5/7/13
*/
public class StringTransform implements ITransformation<String, String> {

	@Override
	public String transform(String s) {
		return  s != null ? s : "";
	}

	@Override
	public String inverse(String object) {
		return (object != null && !object.isEmpty()) ? object : null;
	}
}
