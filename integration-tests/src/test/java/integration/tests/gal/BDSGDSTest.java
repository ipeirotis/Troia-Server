package test.java.integration.tests.gal;

import org.junit.BeforeClass;

public class BDSGDSTest extends DSBaseTestScenario {

    public final static String TEST_NAME = "GDS";
    static DSBaseTestScenario.Setup testSetup;

    @BeforeClass
    public static void setUp() {
        testSetup = new DSBaseTestScenario.Setup("BDS", TEST_NAME, false);
        initSetup(testSetup);
    }
}