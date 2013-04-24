package test.java.integration.tests.gal;

import org.junit.BeforeClass;

public class BMVBarzanMozafariTest extends MVBaseTestScenario {

    public final static String TEST_NAME = "BarzanMozafari";
    static MVBaseTestScenario.Setup testSetup;

    @BeforeClass
    public static void setUp() {
        testSetup = new MVBaseTestScenario.Setup("BMV", TEST_NAME, false);
        initSetup(testSetup);
    }
}
