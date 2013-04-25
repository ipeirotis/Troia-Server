package test.java.integration.tests.gal;

import org.junit.BeforeClass;
import static test.java.integration.tests.gal.IMVAdultContentWithEvaluationTest.testSetup;

public class IMVBarzanMozafariWithEvaluationTest extends MVBaseTestScenario {

    public final static String TEST_NAME = "BarzanMozafariWithEvaluation";

    @BeforeClass
    public static void setUp() {
        testSetup = new MVBaseTestScenario.Setup("IMV", TEST_NAME, true);
        initSetup(testSetup);
    }
}
