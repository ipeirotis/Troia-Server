package test.java.integration.tests.gal;

import org.junit.BeforeClass;

public class IMVAdultContentWithEvaluationTest extends MVBaseTestScenario {

    public final static String TEST_NAME = "AdultContentWithEvaluation";
    static MVBaseTestScenario.Setup testSetup;

    @BeforeClass
    public static void setUp() {
        testSetup = new MVBaseTestScenario.Setup("IMV", TEST_NAME, true);
        initSetup(testSetup);
    }
}
