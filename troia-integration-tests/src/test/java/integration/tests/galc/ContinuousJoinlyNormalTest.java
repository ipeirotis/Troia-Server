package test.java.integration.tests.galc;

import com.datascience.galc.ContinuousIpeirotis;
import org.junit.BeforeClass;

public class ContinuousJoinlyNormalTest extends ContinuousBaseTestScenario {

    public static String TEST_NAME = "joinlynormal";
    static ContinuousBaseTestScenario.Setup testSetup;

    @BeforeClass
    public static void setUp() {
        testSetup = new ContinuousBaseTestScenario.Setup(new ContinuousIpeirotis(), TEST_NAME);
        initSetup(testSetup);
    }
}
