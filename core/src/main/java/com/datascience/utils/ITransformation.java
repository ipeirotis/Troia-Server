package com.datascience.utils;

/**
 * @Author: konrad
 */
public interface ITransformation<A, B> {

	B transform(A object);
	A inverse(B object);
}
