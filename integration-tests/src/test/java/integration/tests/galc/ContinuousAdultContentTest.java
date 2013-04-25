package test.java.integration.tests.galc;

import com.datascience.galc.ContinuousIpeirotis;
import org.junit.BeforeClass;

public class ContinuousAdultContentTest extends ContinuousBaseTestScenario {

    public static final String TEST_NAME = "adultcontent";
    static ContinuousBaseTestScenario.Setup testSetup;

    @BeforeClass
    public static void setUp() {
        testSetup = new ContinuousBaseTestScenario.Setup(new ContinuousIpeirotis(), TEST_NAME);
        initSetup(testSetup);
    }
}
