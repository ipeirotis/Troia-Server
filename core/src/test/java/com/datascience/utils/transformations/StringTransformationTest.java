package com.datascience.utils.transformations;

import com.datascience.utils.transformations.simple.TransformationsFactory;
import org.junit.Assert;

/**
 * User: artur
 * Date: 5/16/13
 */
public class StringTransformationTest extends BaseTransformationTest {

	@Override
	protected TransformationsFactory.ITransformationCreator getCreator() {
		TransformationsFactory.ITransformationCreator creator = TransformationsFactory.create("SIMPLE");
		Assert.assertTrue(creator instanceof TransformationsFactory.StringTransformationCreator);
		return creator;
	}
}
