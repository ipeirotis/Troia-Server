package com.datascience.utils.transformations;

import com.datascience.datastoring.transforms.CoreTransformsFactoriesFactory;
import com.datascience.datastoring.transforms.ICoreTransformsFactory;
import com.datascience.utils.transformations.thrift.ThriftCoreTransformFactory;
import org.junit.Assert;

/**
 * @Author: konrad
 */
public class ThriftCoreTransformsFactoryTest extends BaseTransformationTest {

	@Override
	protected ICoreTransformsFactory getCreator() {
		ICoreTransformsFactory creator = CoreTransformsFactoriesFactory.create("THRIFT");
		Assert.assertTrue(creator instanceof ThriftCoreTransformFactory);
		return creator;
	}
}
