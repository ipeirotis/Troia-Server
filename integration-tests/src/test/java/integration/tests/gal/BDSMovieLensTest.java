package test.java.integration.tests.gal;

import org.junit.BeforeClass;

public class BDSMovieLensTest extends DSBaseTestScenario {

    public final static String TEST_NAME = "MovieLens";
    static DSBaseTestScenario.Setup testSetup;

    @BeforeClass
    public static void setUp() {
        testSetup = new DSBaseTestScenario.Setup("BDS", TEST_NAME, false);
        initSetup(testSetup);
    }
}