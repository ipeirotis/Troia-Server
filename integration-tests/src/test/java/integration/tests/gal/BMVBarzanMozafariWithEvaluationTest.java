package test.java.integration.tests.gal;

import org.junit.BeforeClass;

public class BMVBarzanMozafariWithEvaluationTest extends MVBaseTestScenario {

    public final static String TEST_NAME = "BarzanMozafariWithEvaluation";
    static MVBaseTestScenario.Setup testSetup;

    @BeforeClass
    public static void setUp() {
        testSetup = new MVBaseTestScenario.Setup("BMV", TEST_NAME, true);
        initSetup(testSetup);
    }
}
