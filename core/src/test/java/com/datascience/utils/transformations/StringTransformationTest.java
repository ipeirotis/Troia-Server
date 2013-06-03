package com.datascience.utils.transformations;

import com.datascience.datastoring.transforms.CoreTransformsFactoriesFactory;
import com.datascience.datastoring.transforms.ICoreTransformsFactory;
import com.datascience.datastoring.transforms.SimpleStringCoreTransformsFactory;
import org.junit.Assert;

/**
 * User: artur
 * Date: 5/16/13
 */
public class StringTransformationTest extends BaseTransformationTest {

	@Override
	protected ICoreTransformsFactory<String> getCreator() {
		ICoreTransformsFactory<String> creator = CoreTransformsFactoriesFactory.create("SIMPLE");
		Assert.assertTrue(creator instanceof SimpleStringCoreTransformsFactory);
		return creator;
	}
}
