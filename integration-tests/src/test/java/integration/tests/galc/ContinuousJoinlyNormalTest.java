package test.java.integration.tests.galc;

import com.datascience.galc.ContinuousIpeirotis;
import org.junit.BeforeClass;

import java.io.UnsupportedEncodingException;

public class ContinuousJoinlyNormalTest extends ContinuousBaseTestScenario {

    public static final String TEST_NAME = "joinlynormal";
    static ContinuousBaseTestScenario.Setup testSetup;

    @BeforeClass
    public static void setUp() throws UnsupportedEncodingException {
        testSetup = new ContinuousBaseTestScenario.Setup(new ContinuousIpeirotis(), TEST_NAME);
        initSetup(testSetup);
    }
}
