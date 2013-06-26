package com.datascience.utils;

import com.datascience.core.base.ContValue;
import com.google.common.reflect.TypeToken;

/**
 * @Author: konrad
 */
public class Labels {

	static final public TypeToken<ContValue> contValue = new TypeToken<ContValue>() {};
	static final public TypeToken<String> nominalValue = new TypeToken<String>() {};

	static public <T, P> P returnForKind(TypeToken<T> labelType, P whenNominal, P whenCont) {
		if (contValue == labelType) {
			return whenNominal;
		} else if (nominalValue == labelType) {
			return whenCont;
		}
		throw new IllegalArgumentException("Unknown class: " + labelType);
	}

	static public <T, P> P returnForKind(P whenNominal, P whenCont) {
		TypeToken<T> token = new TypeToken<T>() {};
		return returnForKind(token, whenNominal, whenCont);
	}
}
