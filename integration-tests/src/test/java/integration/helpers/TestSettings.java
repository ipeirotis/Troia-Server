package test.java.integration.helpers;

public abstract class TestSettings {

	public static final String FILEPATH_SEPARATOR = System.getProperty("file.separator");
	
	public static final String GAL_TESTDATA_BASEDIR = System.getProperty("user.dir") + FILEPATH_SEPARATOR + "datasets" + FILEPATH_SEPARATOR + "gal" + FILEPATH_SEPARATOR;
	public static final String GALC_TESTDATA_BASEDIR = System.getProperty("user.dir") + FILEPATH_SEPARATOR + "datasets" + FILEPATH_SEPARATOR + "galc" + FILEPATH_SEPARATOR;
	public static final String GAL_RESULTS_BASEDIR = System.getProperty("user.dir") + FILEPATH_SEPARATOR + "results" + FILEPATH_SEPARATOR + "gal" + FILEPATH_SEPARATOR;
	public static final String GALC_RESULTS_BASEDIR = System.getProperty("user.dir") + FILEPATH_SEPARATOR + "results" + FILEPATH_SEPARATOR + "galc" + FILEPATH_SEPARATOR;
}
