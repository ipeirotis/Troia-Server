package test.java.integration.tests.gal;

import org.junit.BeforeClass;

public class IDSBarzanMozafariWithEvalutionTest extends DSBaseTestScenario {

    public final static String TEST_NAME = "BarzanMozafariWithEvaluation";
    static DSBaseTestScenario.Setup testSetup;

    @BeforeClass
    public static void setUp() {
        testSetup = new DSBaseTestScenario.Setup("IDS", TEST_NAME, true);
        initSetup(testSetup);
    }
}
