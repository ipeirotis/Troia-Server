package com.datascience.gal.generator.testsuite;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Get Another Label Test Generator
 */

/**
 * @author Michael Arshynov
 *
 */
public class Main {
	private static String MSG_NO_PARAMS = 	"========================Get Another Label Test Generator========================\n\n"+
			"--------------------------------Input parameters--------------------------------\n" +
			"   Case insensitive. Order does not matter. As usual :) \n\n" +
			" -?            :   Auxiliary Help with Examples. The flag.\n"+
			" -a            :   Help for 'a' type generation. The flag.\n" +
			" -b            :   Help for 'b' type generation. The flag.\n" +
			" -c            :   Help for 'c' type generation. The flag.\n" +
			" -d            :   Help for 'd' type generation. The flag.\n" +
			" -e            :   Help for 'e' type generation. The flag.\n" +
			" -f            :   Help for 'f' type generation. The flag.\n\n" +
			" o (optional)  :   Path to the file where to put generated data.\n" +
			"                   By default for all types files are named as\n" +
			"                   'out_a.txt', 'out_b.txt' ... 'out_f.txt', respectively.\n\n"+
			" Type          :   Generation type. Could be 'a', 'b', 'c'. For more info\n" +
			"                   take a look at '-?'.\n" +
			" C             :   The number of categories. Are Useful for types 'a' and 'b'.\n" +
			" K             :   The number of examples to generate. Is Useful for type 'a'.\n" +
			" priors (opt.) :   Path to the file contains priorities (see '-?' or '-a').\n" +
			"                   Is Actual for 'a' type of generation.\n" +
			"                   Expected quality of Workers (lets say, maximal.\n" +
			"                   Is Actual for 'b' type):\n" +
			" qa            :   bottom value (in range from 0.5 to 0.98).\n" +
			" qb            :   top value (in range from 'qb' to 1).\n" +
			"                   Condition qa<qb must be filfilled.\n\n" +
			" w             :   Path to file which contains Workers. Is Actual for 'c', 'f' \n" +
			"                   types. Use resulted file after 'b' type of test running.\n" +
			" CD            :   Correct Data. Path to file. Is useful for type 'c' as well\n" +
			"                   as for 'd' or 'e'. You may use generated file after running\n" +
			"                   'a' type of the Generation method. \n" +
			" L             :   Count of Labels assigned to each example. Is Actual for 'c'." +
		  "\n p             :   Number between 0 and 1, number of lines to keep gold data.\n" +
		    "                   Is Useful for 'd'\n" +
			" CDdsas        :   Path to file which contains output for the labels from the\n" +
			"                   DSaS algorithm. Is Useful for 'e'\n" +
			" wdsas         :   Path to file which contains output for workers from the\n" +
			"                   DSaS algorithm. Is Useful for 'f'\n" +
			"\n\n" +
			"----------LOOKS complicated, HUH? NO WAY! JUST TAKE A LOOK AT EXAMPLES:---------\n" +
			"TYPE A:          C=5 k=1000 priors=prior_list.txt\n" +
			"TYPE B:          C=5 qa=0.5 qb=0.71 o=workers.txt\n" +
			"TYPE C:          W=workers.txt cd=out_a.txt l=20 o=assigned_labels.txt\n" +
			"TYPE D:          cd=out_a.txt p=1\n" +
			"TYPE E:          CDDSaS=results_dsas.txt CD=out_a.txt\n" +
			"TYPE F:          w=workers.txt wDsAs=workers_dsas.txt\n";

	private static String MSG_SUCCESS = "\nSuccess! Please check %s for results. Number of Generated tests: %s\n";
	
	private static void printNOPARAMS() {
		System.out.println(MSG_NO_PARAMS);
	}
	
	private static void printSuccess(String path, String numberOfTests) {
		System.out.format(MSG_SUCCESS, new Object[]{path, numberOfTests});
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			printNOPARAMS();
		} else {
			String error = validateAndDoJob(args);
			if (error==null) {
//				System.out.println("TODO: IMPLEMENT ");
			} else {
				System.out.println(error);
				printNOPARAMS();
			}
			
//			printSuccess("c:\\out.txt", "10");
//			for (String arg: args) {
//				System.out.println("ARG="+arg);
//			}
		}
	}
	
	private static final String PARAM_OUTPUT_FILE = "o";
	private static final String PARAM_PRIORS_FILE = "priors";
    private static final String PARAM_CATEGORIES_COUNT = "c";
    private static final String PARAM_NUMBER_OF_TESTS = "k";
    private static final String PARAM_WORKERS = "w";
    private static final String PARAM_LABELS = "l";
    private static final String PARAM_CORRECT_DATA = "cd";
    private static final String PARAM_NUMBER_OF_LINES = "p";
    private static final String PARAM_WORKERS_DSAS = "wdsas";
    private static final String PARAM_CORRECT_DATA_DSAS = "cddsas";
    
    private static final String PARAM_QUALITY_A = "qa";
    private static final String PARAM_QUALITY_B = "qb";
    private static final String VALUE_FILE_BY_DEFAULT_POSTFIX = "_out.txt";
    
	private static final String FLAG_HELP = "-?";
	private static final String flagsHelp[] = { FLAG_HELP,"-a","-b","-c","-d","-e","-f" };
	private static final String types[] = { "a","b","c","d","e","f" };
	private static final String parametersCommon[] = { "type" };
	private static final String parametersCommonOptional[] = { PARAM_OUTPUT_FILE };
	private static final String parametersA[] = { PARAM_CATEGORIES_COUNT, PARAM_NUMBER_OF_TESTS };
	private static final String parametersAOptional[] = { PARAM_PRIORS_FILE };
	private static final String parametersB[] = { PARAM_CATEGORIES_COUNT, PARAM_QUALITY_A, PARAM_QUALITY_B };	
	private static final String parametersC[] = { PARAM_WORKERS, PARAM_CORRECT_DATA, PARAM_LABELS };
	private static final String parametersD[] = { PARAM_CORRECT_DATA, PARAM_NUMBER_OF_LINES };
	private static final String parametersE[] = { PARAM_CORRECT_DATA, PARAM_CORRECT_DATA_DSAS };
	private static final String parametersF[] = {  PARAM_WORKERS, PARAM_WORKERS_DSAS };
	
	private static Map<String, String> map = new HashMap<String, String>();

	private static String selectedType = null;
	/**
	 * @param args
	 * @return
	 */
	private static String validateAndDoJob(final String[] args) {
		final String INVALID_SYNTAX = "Invalid syntax. Value-pair sequence is broken. ";
		final String DUPLICATED_OPTION = "Duplicated option has been found, please check again the set of parameters.";
		final String WRONG_GENERATION_TYPE = "Unknown Generation Type or Generation Type is absent at all. ";
		final String MULTIPLE_TYPES = "You have typed multiple count of types, please chose one for generation, or use -?";
		
		for (String parameter: args) {
			String keyValue[] = parameter.split("=");
			if ( keyValue==null) return INVALID_SYNTAX;
			if (keyValue.length==1) {
				if (isFlag(keyValue[0])) {
					map.put(keyValue[0], null);
					continue;
				}
				return INVALID_SYNTAX;
			} else {
				if (keyValue.length>2) return INVALID_SYNTAX; 
				map.put(keyValue[0], keyValue[1]);
			}
		}
		
		if (map.size()!=args.length) {
			return DUPLICATED_OPTION;
		}
		
		Set<String> flags = findoutKnownFlags(map);
		List<String> type = findoutType(map);

		if (flags!=null && flags.size()>0) {
			for (String flag:flags) {
				if (FLAG_HELP.equalsIgnoreCase(flag) && flags.size()>1) {
					System.out.println("More than one flag is set, that is why flag \"?\" is skipped");
				} else {
					
					final String[] helpMessages = {
							"---GALTSG help section---\n GALTSG is acronym of The GAL Test Suite Generator.\n" +
							"GAL is another acronym of the GetAssignedLabel project, which do\n" +
							"employee`s quality of work evaluation\n\n" +
							" Tool has 6 types of the test generation and comparison of results:\n" +
							"   Type A: Creates Objects.\n" +
							"   Type B: Creates Workers.\n" +
							"   Type C: Creates Assigned Labels.\n" +
							"   Type D: Creates Gold Labels.\n" +
							"   Type E: Compare Labels.\n" +
							"   Type F: Compare Workers.\n" +
							"For more Information about any type please use -<type> flag. Case insensitive.\n",
						//A	
							"---Help section for the 'a' type of the GAL Test Suite generation---\n" +
							"   Note: Some parameters are optional and marked as 'opt.'\n" +
							" Purpose: Creates Data (Objects which belong to the one of the category).\n" +
							" Inputs:\n" +
							"            o (opt.):   File name to store results, by default 'a_out.txt'.\n" +
							"            c:          The number of categories\n" +
							"            k:          The number of test generated\n" +
							"            priors :    The prior probabilities for each of the categories (see\n" +
							"             (opt.)     prior.txt in the GetAnotherLabel). If not specified,\n" +
							"                        allocates equal probabilities to each category.\n" +
							"                        Example of Valid file with priors (for c=5):\n" +
							"                           0.1\n" +
							"                           0.63\n" +
							"                           0.17\n" +
							"                           0.05\n" +
							"                           0.05\n" +
							" Outputs:   A text file with K lines, each one containing a separate object.\n",
						//B	
							"---Help section for the 'b' type of the GAL Test Suite generation---\n" +
							" Purpose: Creates Workers.\n" +
							" Inputs:\n" +
							"            o (opt.):   File name to store results, by default 'b_out.txt'.\n" +
							"            c:          The number of categories\n" +
							"            qa:         Bottom level of the expected quality of the workers\n" +
							"                        (in range from 0.5 to 0.98).\n" +
							"            qb          Top level of the expected quality of the workers\n" +
							"                        (in range from 'qa' to 1).\n" +
							" Outputs:\n" +
							"           * A file with the confusion matrixes of the workers. A confusion\n" +
							"            matrix is a double[C][C] matrix and the element [i][j] shows the\n" +
							"           probability that when the worker sees an example of the correct\n" +
							"           category will classify it as j.\n" +
							"           * A file with the accuracy, quality_opt, quality_avg of the workers,\n" +
							"           computed using the confusion matrix of the worker.\n",
						//C
							"---Help section for the 'c' type of the GAL Test Suite generation---\n" +
							" Purpose: Create Assigned Labels.\n" +
							" Inputs:\n" +
							"            o (opt.):   File name to store results, by default 'c_out.txt'.\n" +
							"            w:          Path to file which contains Workers. Use resulted file\n" +
							"                        after 'b' type of test running.\n" +
							"            cd:         Correct Data. Path to file.  You may use generated\n" +
							"                        file after running 'a' type of the Generation method.\n" +
							"            l:          Count of Labels assigned to each example.\n" +
							" Outputs:   * The set of Assigned Labels\n",
						//D	
							"---Help section for the 'd' type of the GAL Test Suite generation---\n"+
							" Purpose: Create Gold Labels\n" +
							" Inputs:\n" +
							"            o (opt.):   File name to store results, by default 'd_out.txt'.\n" +
							"            cd:         Correct Data. Path to file.  You may use generated\n" +
							"                        file after running 'a' type of the Generation method.\n" +
							"            p:          Number of lines between 0 and 1 to keep gold data.\n" +
							" Outputs:   * A file contains some gold data, approximately in size p*K lines.\n",
						//E
							"---Help section for the 'e' type of the GAL Test Suite generation---\n"+
							" Purpose: Compares Labels\n" +
							" Inputs:\n" +
							"            o (opt.):   File name to store results, by default 'e_out.txt'.\n" +
							"            cd:         Correct Data. Path to file.  You may use generated\n" +
							"                        file after running 'a' type of the Generation method.\n" +
							"            cddsas      Path to file which contains output for the labels from\n" +
							"                        the DSaS algorithm\n" +
							" Outputs:\n" +
							"            * A confusion matrix int[C][C] resuts, with the element [i][j]\n" +
							"            being equal to the number of objects with correct category Ci being\n" +
							"            classified by the DSaS algorithm as Cj.\n" +
							"            * A set of measurements: (a) The Expected Misclassification Cost,\n" +
							"            given the overall confusion matrix, (b) The number of correctly and\n" +
							"            incorrectly classified examples\n" ,
						//F
							"---Help section for the 'f' type of the GAL Test Suite generation---\n"+
							" Purpose: Compares Workers\n" +
							" Inputs:\n" +
							"            o (opt.):   File name to store results, by default 'f_out.txt'.\n" +
							"            w:          Path to file which contains Workers. Use resulted file\n" +
							"                        after 'b' type of test running.\n" +
							"            wdsas       Path to file which contains output for workers from\n" +
							"                        the DSaS algorithm.\n" +
							" Outputs:\n" +
							"            * A text file that contains one line per worker.\n" +
							"            * A text file that lists the original and the estimated confusion\n" +
							"            matrix for each worker.\n" +
							"            * A set of measurements: The average difference of worker_quality\n" +
							"            from the original file and the estimated.\n",
					};
					System.out.println(helpMessages[getFlagPos(flag)]);
				}
			}
			selectedType = null;
			return null;
		}
		
		if (type==null || type.size() == 0) {
			return WRONG_GENERATION_TYPE;
		} else if (type.size()>1){
			return MULTIPLE_TYPES;
		} else {
			String checkParameters = checkGenType(type.get(0), map);
			if (checkParameters==null) {
				selectedType = type.get(0);
				System.out.println(doGeneration(selectedType, map));
			} else {
				System.out.println(checkParameters);
			}
		}
		return null;
	}

	/** Finds position of the given flag in flagsHelp array of known flags
	 * @param flag
	 * @return position, integer
	 */
	private static int getFlagPos(String flag) {
		int pos = 0;
		for (String item: flagsHelp) {
			if (item.equalsIgnoreCase(flag)) return pos;
			pos++;
		}
		return -1;
	}

	/** Generates Test Suits according to the type and parameters
	 * @param type
	 * @param map
	 * @return information about progress
	 */
	private static String doGeneration(String type, Map<String, String> map) {
		String o = type+VALUE_FILE_BY_DEFAULT_POSTFIX;
		String priors = null;
		String error = "generation type '"+type+"' is unknown";
		
		String numberOfCategoriesStr = map.get(PARAM_CATEGORIES_COUNT);
		String numberOfTestsStr = map.get(PARAM_NUMBER_OF_TESTS);
		String qaStr = map.get(PARAM_QUALITY_A);
		String qbStr = map.get(PARAM_QUALITY_B);
		String w = map.get(PARAM_WORKERS);
		String lStr = map.get(PARAM_LABELS);
		String cd = map.get(PARAM_CORRECT_DATA);
		String pStr = map.get(PARAM_NUMBER_OF_LINES);
		String cddsas = map.get(PARAM_CORRECT_DATA_DSAS);
		String wdsas = map.get(PARAM_WORKERS_DSAS);
		
		int c = -1;
		int k = -1;
		double qa = -1;
		double qb = -1;
		int l = -1;
		int p = -1;
		// A
		if (type.equalsIgnoreCase("a")) {

			try {
				c = Integer.valueOf(numberOfCategoriesStr);
			} catch(NumberFormatException nfExc) {
				return "'c' parameter is wrong.";
			}
			
			try {
				k = Integer.valueOf(numberOfTestsStr);
			} catch(NumberFormatException nfExc) {
				return "'k' parameter is wrong.";
			}
			if (map.containsKey(PARAM_PRIORS_FILE)) {
				priors = map.get(PARAM_PRIORS_FILE);
				error = checkFilePath(priors, type, false);
				if (error!=null) {
					return "'priors' parameter is wrong: "+error;
				}
			}
			if (map.containsKey(PARAM_OUTPUT_FILE)) {
				o = map.get(PARAM_OUTPUT_FILE);
				error = checkFilePath(o, type, true);
				if (error!=null) {
					return "'o' parameter is wrong: "+error;
				} else
					System.out.println("file '"+o+"' has been created");
			}
			error = TestSuitGeneratorHelper.validateTestA(o, c, k, priors);
			if (error!=null) {
				return error;
			} else {
				error = TestSuitGeneratorHelper.generateTestA(o, c, k, priors);
			}
			
		}// A
		// B
		if (type.equalsIgnoreCase("b")) {
			try {
				c = Integer.valueOf(numberOfCategoriesStr);
			} catch(NumberFormatException nfExc) {
				return "'c' parameter is wrong.";
			}
			try {
				qa = Double.parseDouble(qaStr);
			} catch(NumberFormatException nfExc) {
				return "'qa' parameter is wrong.";
			}
			try {
				qb = Double.parseDouble(qbStr);
			} catch(NumberFormatException nfExc) {
				return "'qb' parameter is wrong.";
			}
			if (map.containsKey(PARAM_OUTPUT_FILE)) {
				o = map.get(PARAM_OUTPUT_FILE);
				error = checkFilePath(o, type, true);
				if (error!=null) {
					return "'o' parameter is wrong: "+error;
				} else
					System.out.println("file '"+o+"' has been created");
			}
			error = TestSuitGeneratorHelper.validateTestB(o, c, qa, qb);
			if (error!=null) {
				return error;
			} else {
				error = TestSuitGeneratorHelper.generateTestB(o, c, qa, qb);
			}
		} // B
		// C
		if (type.equalsIgnoreCase("c")) {
			try {
				l = Integer.valueOf(lStr);
			} catch(NumberFormatException nfExc) {
				return "'l' parameter is wrong.";
			}
			if (w!=null) {
				error = checkFilePath(w, type, false);
				if (error!=null) {
					return "'w' parameter is wrong: "+error;
				}
			} else return "'w' should not be empty";
			if (cd!=null) {
				error = checkFilePath(cd, type, false);
				if (error!=null) {
					return "'cd' parameter is wrong: "+error;
				}
			} else return "'cd' should not be empty";
			if (map.containsKey(PARAM_OUTPUT_FILE)) {
				o = map.get(PARAM_OUTPUT_FILE);
				error = checkFilePath(o, type, true);
				if (error!=null) {
					return "'o' parameter is wrong: "+error;
				} else
					System.out.println("file '"+o+"' has been created");
			}
			error = TestSuitGeneratorHelper.validateTestC(o, w, cd, l);
			if (error!=null) {
				return error;
			} else {
				error = TestSuitGeneratorHelper.generateTestC(o, w, cd, l);
			}
		} // C
		// D
		if (type.equalsIgnoreCase("d")) {
			if (cd!=null) {
				error = checkFilePath(cd, type, false);
				if (error!=null) {
					return "'cd' parameter is wrong: "+error;
				}
			} else 
			if (pStr!=null) {
				try {
				p = Integer.parseInt(pStr);
				if (p!=1 || p!=0) {
					return "'p' should be or '0' either '1'.";
				}
				} catch (NumberFormatException nfExc) {
					return "'p' parameter is wrong: "+nfExc; 
				}
			} else return "'p' should not be empty";
			if (map.containsKey(PARAM_OUTPUT_FILE)) {
				o = map.get(PARAM_OUTPUT_FILE);
				error = checkFilePath(o, type, true);
				if (error!=null) {
					return "'o' parameter is wrong: "+error;
				} else
					System.out.println("file '"+o+"' has been created");
			}
			error = TestSuitGeneratorHelper.validateTestD(o, cd, p);
			if (error!=null) {
				return error;
			} else {
				error = TestSuitGeneratorHelper.generateTestD(o, cd, p);
			}
		} // D
		// E
		if (type.equalsIgnoreCase("e")) {
			if (cd!=null) {
				error = checkFilePath(cd, type, false);
				if (error!=null) {
					return "'cd' parameter is wrong: "+error;
				}
			} else return "'cd' should not be empty";
			if (cddsas!=null) {
				error = checkFilePath(cddsas, type, false);
				if (error!=null) {
					return "'cddsas' parameter is wrong: "+error;
				}
			} else return "'cddsas' should not be empty";
			if (map.containsKey(PARAM_OUTPUT_FILE)) {
				o = map.get(PARAM_OUTPUT_FILE);
				error = checkFilePath(o, type, true);
				if (error!=null) {
					return "'o' parameter is wrong: "+error;
				} else
					System.out.println("file '"+o+"' has been created");
			}
			error = TestSuitGeneratorHelper.validateTestE(o, cd, cddsas);
			if (error!=null) {
				return error;
			} else {
				error = TestSuitGeneratorHelper.generateTestE(o, cd, cddsas);
			}
		} // E
		// F
		if (type.equalsIgnoreCase("f")) {
			if (w!=null) {
				error = checkFilePath(w, type, false);
				if (error!=null) {
					return "'w' parameter is wrong: "+error;
				}
			} else return "'w' should not be empty";
			if (wdsas!=null) {
				error = checkFilePath(wdsas, type, false);
				if (error!=null) {
					return "'wdsas' parameter is wrong: "+error;
				}
			} else return "'wdsas' should not be empty";
			if (map.containsKey(PARAM_OUTPUT_FILE)) {
				o = map.get(PARAM_OUTPUT_FILE);
				error = checkFilePath(o, type, true);
				if (error!=null) {
					return "'o' parameter is wrong: "+error;
				} else
					System.out.println("file '"+o+"' has been created");
			}
			error = TestSuitGeneratorHelper.validateTestF(o, w, wdsas);
			if (error!=null) {
				return error;
			} else {
				error = TestSuitGeneratorHelper.generateTestF(o, w, wdsas);
			}
		} // F
		return error;
	}

	/** Check whether given path is existing or not, if not is it possible to create that file
	 * @param pathWithFileName
	 * @param type
	 * @return true if file is able to be created
	 */
	private static String checkFilePath(final String pathWithFileName, String type, boolean isNeedToEdit) {
		final String header = new Date().toString()+"\n"+"Resulted file for type '"+type+"' generation"+"\n";
		final String pathInQuotes = "'"+pathWithFileName+"' ";
		final String ERROR_MSG_PREFIX = "Unable to create Output File by path "+pathInQuotes;
		final String ERROR_MSG_READ_PREFIX = "Unable to read Output File by path "+pathInQuotes;
		File f = null;
		
		try {
			f = new File(pathWithFileName);
			String currentDir = System.getProperty("user.dir");
			if (!f.isAbsolute()) {
				f = new File(currentDir+File.separatorChar+pathWithFileName);
				System.out.println("full path="+currentDir+File.separatorChar+pathWithFileName);
			}
		} catch(NullPointerException npExc) {
			return ERROR_MSG_PREFIX+", wrong path.";
		}
		try {
			if (f.isDirectory()) return ERROR_MSG_PREFIX+", the file denoted by this abstract pathname is a directory.";
			
			if (isNeedToEdit) {
				FileWriter fw;
				if (f.exists()) {
					try {
						fw = new FileWriter(f);
						fw.flush();
						fw.append(header);
						fw.close();
					} catch (IOException e) {
						return ERROR_MSG_PREFIX+", an I/O error occurs.";
					}
				} else {
					try {
						fw = new FileWriter(f);
						fw.flush();
						fw.append(new Date().toString());
						fw.close();
					} catch (IOException e) {
						return ERROR_MSG_PREFIX+", an I/O error occurs.";
					}
				}
			} else {
				try {
					FileReader fr = new FileReader(f);
					if (fr.read()<1) {
						return ERROR_MSG_READ_PREFIX+". File is not ready to be read.";
					}
					fr.close();
				} catch (FileNotFoundException e) {
					return ERROR_MSG_READ_PREFIX+". Given file path points on absent object";
				} catch (IOException e) {
					return ERROR_MSG_READ_PREFIX+", an I/O error occurs.";
				}
			}
		} catch (SecurityException sExc) {
			return isNeedToEdit?ERROR_MSG_PREFIX:ERROR_MSG_READ_PREFIX+". Method denies read access to the file.";
		}
		return null;
	}

	/** Check required parameters for exact type  
	 * @param type
	 * @return false if all required parameters are present, error message otherwise
	 */
	private static String checkGenType(final String type, final Map<String, String> map) {
		final String NOT_ENOUGH_PARAMETERS = "Not enough parameters for type'%s', for more Info you may try '-%s' or '-?'";
		String[] params = null;
		
		if (type.equalsIgnoreCase("a")) {
			params = parametersA;
		}
		if (type.equalsIgnoreCase("b")) {
			params = parametersB;			
		}
		if (type.equalsIgnoreCase("c")) {
			params = parametersC;
		}
		if (type.equalsIgnoreCase("d")) {
			params = parametersD;	
		}
		if (type.equalsIgnoreCase("e")) {
			params = parametersE;		
		}
		if (type.equalsIgnoreCase("f")) {
			params = parametersF;
		}
		
		if (ifParametersAreAppropriate(type, params, map)) {
			return null;
		}
		System.out.printf(NOT_ENOUGH_PARAMETERS, type, type);
		return "";
	}

	/**
	 * Checks if parameter set is appropriate to the given type
	 * @param parameters
	 */
	private static boolean ifParametersAreAppropriate(final String type, String[] parameters, final Map<String, String> map) {
		int mainParamsCount = 0;
		int optionalParamsCount = 0;
		for (String option: map.keySet()) {
			if (isInSet(option, parameters)) {
				mainParamsCount++;
				continue;
			} else if(isInSet(option, parametersCommon)) {
				mainParamsCount++;
				continue;
			} else if (isInSet(option, parametersCommonOptional)) {
				optionalParamsCount++;
				continue;
			} else {
				if (type.equalsIgnoreCase("a") && isInSet(option, parametersAOptional)) {
					optionalParamsCount++;
					continue;
				}
				return false;
			}
		}
		return (mainParamsCount - 1 == parameters.length);
	}

	/**
	 * Checks if parameter belongs to the set
	 * @param option
	 * @param parameters
	 * @return
	 */
	private static boolean isInSet(String option, String[] parameters) {
		for (String parameter: parameters) {
			if (parameter.equalsIgnoreCase(option)) {
				return true;
			}
			continue;
		}
		return false;
	}

	/**
	 * Checks If parameter has no value, only key
	 * @param keyValue
	 * @return true is value is a known flag
	 */
	private static boolean isFlag(String keyValue) {
		for (String flag:flagsHelp) {
			if (flag.equalsIgnoreCase(keyValue)) return true;
		}
		return false;
	}

	/** 
	 * Finds out flag list. If list is empty then flag(-s) is(are) absent.
	 * @param map
	 * @return known flag list or null if unknown flag has been found 
	 */
	private static Set<String> findoutKnownFlags(final Map<String, String> map) {
		Set<String> set = new HashSet<String>();
		for (String key: map.keySet()) {
			if (isFlag(key)) set.add(key);
		}
		return set;
	}

	/** 
	 * Finds out the type of generation
	 * @param map
	 * @return list of types or null if some type is unknown or are absent at all
	 */
	private static List<String> findoutType(final Map<String, String> map) {
		List<String> ret = null;
		for (String key: map.keySet()) {
			if (key!=null && key.equalsIgnoreCase(parametersCommon[0])) {
				String type = map.get(key);
				for (String typeKnown: types) {
					if (type.equalsIgnoreCase(typeKnown)) {
						if (ret == null) 
							ret = new ArrayList<String>();
						ret.add(typeKnown);
					}
				}
			}
		}
		return ret;
	}

	/** 
	 * Checks if there is any unknown flag
	 * @param map
	 * @return false if unknown option has been found
	 */
	private static boolean findoutUnknownOption(Map<String, String> map) {
		for (String key: map.keySet()) {
			boolean isUnknown = true;
			// check if flag 
			for (String flag: flagsHelp) {
				if (flag.equalsIgnoreCase(key)) {
					if (map.get(flag)!=null) {
						return true;
					}
				}
			}		
		}
		return false;
	}

}
