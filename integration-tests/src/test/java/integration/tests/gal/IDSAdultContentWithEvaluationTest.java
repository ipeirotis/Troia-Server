package test.java.integration.tests.gal;

import org.junit.BeforeClass;

public class IDSAdultContentWithEvaluationTest extends DSBaseTestScenario {

    public final static String TEST_NAME = "AdultContentWithEvaluation";
    static DSBaseTestScenario.Setup testSetup;

    @BeforeClass
    public static void setUp() {
        testSetup = new DSBaseTestScenario.Setup("IDS", TEST_NAME, true);
        initSetup(testSetup);
    }
}
