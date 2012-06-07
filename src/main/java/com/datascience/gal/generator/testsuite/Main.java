package com.datascience.gal.generator.testsuite;

import java.util.ArrayList;
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
			" qa            :   bottom value (in range of 0.5 to 1)\n" +
			" qb            :   top value (in range of 0.5 to 1)\n" +
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
			String error = validate(args);
			for (String key: map.keySet()) {
				System.out.println(key+"=>"+map.get(key));
			}
			if (error==null) {
				System.out.println("TODO: IMPLEMENT ");
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

	private static final String FLAG_HELP = "-?";
	private static final String flagsHelp[] = { FLAG_HELP,"-a","-b","-c","-d","-e","-f" };
	private static final String types[] = { "a","b","c","d","e","f" };
	private static final String parametersCommon[] = { "type" };
	private static final String parametersCommonOptional[] = { "o" };
	private static final String parametersA[] = { "c", "k" };
	private static final String parametersAOptional[] = { "priors" };
	private static final String parametersB[] = { "c", "qa", "qb" };	
	private static final String parametersC[] = { "w", "cd", "l" };
	private static final String parametersD[] = { "cd", "p" };
	private static final String parametersE[] = { "cd", "cddsas" };
	private static final String parametersF[] = {  "w", "wdsas"};
	
	private static Map<String, String> map = new HashMap<String, String>();
	
	/**
	 * @param args
	 * @return
	 */
	private static String validate(final String[] args) {
		final String INVALID_SYNTAX = "Invalid syntax. Value-pair sequence is broken. ";
		final String DUPLICATED_OPTION = "Duplicated option has been found, please check again the set of parameters.";
		final String WRONG_GENERATION_TYPE = "Unknown Generation Type or Generation Type is absent at all. ";
		
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
		System.out.println("findoutUnknown="+findoutUnknownOption(map));
		
		Set<String> flags = findoutKnownFlags(map);
		String type = findoutType(map);
		if (type==null) {
			if (flags!=null && flags.size()>0) {
				for (String flag:flags) {
					if (FLAG_HELP.equalsIgnoreCase(flag) && flags.size()>1) {
						System.out.println("More than one flag is set, that is why flag \"?\" is skipped");
					} else
					System.out.println("++++=HELP SECTION for "+flag+"++++");	
				}
				return null;
			}
			return WRONG_GENERATION_TYPE;
		}
		
		if (map.size()!=args.length) {
			return DUPLICATED_OPTION;
		}
		return null;
	}

	/**
	 * @param keyValue
	 * @return true is value is a know flag
	 */
	private static boolean isFlag(String keyValue) {
		for (String flag:flagsHelp) {
			if (flag.equalsIgnoreCase(keyValue)) return true;
		}
		return false;
	}

	/** Finds out flag list. If list is empty then flag(-s) is(are) absent.
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

	/** Finds out the type of generation
	 * @param map
	 * @return type or null if type is unknown or is absent at all
	 */
	private static String findoutType(final Map<String, String> map) {
		for (String key: map.keySet()) {
			if (key!=null && key.equalsIgnoreCase(parametersCommon[0])) {
				String type = map.get(key);
				for (String typeKnown: types) {
					if (type.equalsIgnoreCase(typeKnown)) {
						return typeKnown;
					}
				}
			}
		}
		return null;
	}

	/** Checks if there is any unknown flag
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
//    flagsHelp[] = { "-?","-a","-b","-c","-d","-e","-f" };
//    types[] = { "a","b","c","d","e","f" };
//    parametersCommon[] = { "type" };
//    parametersCommonOptional[] = { "o" };
//    parametersA[] = { "c", "k" };
//    parametersAOptional[] = { "priors" };
//    parametersB[] = { "c", "qa", "qb" };	
//    parametersC[] = { "w", "cd", "l" };
//    parametersD[] = { "cd", "p" };
//    parametersE[] = { "cd", "cddsas" };
//    parametersF[] = {  "w", "wdsas"};			
				
		}
//		map.clear();
		return false;
	}

}
