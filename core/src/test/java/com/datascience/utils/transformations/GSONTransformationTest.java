package com.datascience.utils.transformations;

import com.datascience.datastoring.transforms.CoreTransformsFactoriesFactory;
import com.datascience.datastoring.transforms.ICoreTransformsFactory;
import com.datascience.datastoring.transforms.SerializerBasedCoreTransformsFactory;
import org.junit.Assert;

/**
 * User: artur
 * Date: 5/16/13
 */
public class GSONTransformationTest extends BaseTransformationTest {

	@Override
	protected ICoreTransformsFactory<String> getCreator() {
		ICoreTransformsFactory<String> creator = CoreTransformsFactoriesFactory.create("JSON");
		Assert.assertTrue(creator instanceof SerializerBasedCoreTransformsFactory);
		return creator;
	}
}
