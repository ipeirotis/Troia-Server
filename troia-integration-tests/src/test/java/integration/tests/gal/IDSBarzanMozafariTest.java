package test.java.integration.tests.gal;

import org.junit.BeforeClass;

public class IDSBarzanMozafariTest extends DSBaseTestScenario {

    public final static String TEST_NAME = "BarzanMozafari";
    static DSBaseTestScenario.Setup testSetup;

    @BeforeClass
    public static void setUp() {
        testSetup = new DSBaseTestScenario.Setup("IDS", TEST_NAME, false);
        initSetup(testSetup);
    }
}
