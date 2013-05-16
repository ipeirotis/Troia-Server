package com.datascience.utils.transformations;

/**
 * User: artur
 * Date: 5/16/13
 */
public class StringTransformationTest extends BaseTransformationTest {

	@Override
	protected TransformationsFactory.ITransformationCreator getCreator() {
		return TransformationsFactory.create("STRING");
	}
}
