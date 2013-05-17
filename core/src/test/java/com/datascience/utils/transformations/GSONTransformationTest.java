package com.datascience.utils.transformations;

import org.junit.Assert;
import org.junit.Ignore;

/**
 * User: artur
 * Date: 5/16/13
 */
@Ignore
public class GSONTransformationTest extends BaseTransformationTest {

	@Override
	protected TransformationsFactory.ITransformationCreator getCreator() {
		TransformationsFactory.ITransformationCreator creator = TransformationsFactory.create("GSON");
		Assert.assertTrue(creator instanceof TransformationsFactory.SerializationBasedTransformationCreator);
		return creator;
	}
}
