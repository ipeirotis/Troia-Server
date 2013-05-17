package com.datascience.utils.transformations;

import org.junit.Assert;

/**
 * User: artur
 * Date: 5/16/13
 */
public class StringTransformationTest extends BaseTransformationTest {

	@Override
	protected TransformationsFactory.ITransformationCreator getCreator() {
		TransformationsFactory.ITransformationCreator creator = TransformationsFactory.create("STRING");
		Assert.assertTrue(creator instanceof TransformationsFactory.StringTransformationCreator);
		return creator;
	}
}
