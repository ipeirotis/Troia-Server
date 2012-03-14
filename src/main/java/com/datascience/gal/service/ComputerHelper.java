/**
 * 
 */
package com.datascience.gal.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.datascience.gal.AssignedLabel;
import com.datascience.gal.BatchDawidSkene;
import com.datascience.gal.Category;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.MisclassificationCost;

/**
 * @author Michael Arshynov
 *
 */
public class ComputerHelper {

	/**
	 * @param arg
	 * @return
	 */
	private static boolean isEmpty(String arg) {
		return (arg==null || arg.isEmpty());
	}
	
	private static Set<Category> categorySet = null;
	private static Set<AssignedLabel> assignedLabelSet = null;
	private static Set<CorrectLabel> correctLabelSet = null;
	private static Set<MisclassificationCost> costSet = null;
	static int iterationsInt = -1;
	/**
	 * @param categories
	 * @param unlabeled
	 * @param gold
	 * @param costs
	 * @param iterations
	 * @return
	 * @see {@link com.datascience.gal.scripts.Main#main(String[])}
	 */
	public static synchronized String compute(String categories, String unlabeled, String gold, String costs, String iterations) {
		String result = validate(categories, unlabeled, gold, costs, iterations);
		if (result.trim().length()==0) {
			result = calculateAndPrint().toString();
		}
		return result;
	}
	
	/**
	 * 
	 */
	private static StringBuffer calculateAndPrint() {
		StringBuffer ret = new StringBuffer("");
		boolean verbose = true;
		DawidSkene ds = new BatchDawidSkene(0 + "", categorySet);
        for (MisclassificationCost mcc : costSet) {
            ds.addMisclassificationCost(mcc);
        }
        for (AssignedLabel l : assignedLabelSet) {
            ds.addAssignedLabel(l);
        }
        for (CorrectLabel l : correctLabelSet) {
            ds.addCorrectLabel(l);
        }
        Map<String, String> prior_voting = ds.getMajorityVote();
        
        ret.append(saveMajorityVote(verbose, ds));
        for (int i = 0; i < iterationsInt; i++) {
            // ds.estimate(iterations);
            ds.estimate(1);
        }
        
        ret.append(saveWorkerQuality(verbose, ds));

        ret.append(saveObjectResults(verbose, ds));

        ret.append(saveCategoryPriors(verbose, ds));

        ret.append(saveDawidSkeneVote(verbose, ds));
        
        Map<String, String> posterior_voting = ds.getMajorityVote();

        ret.append(saveDifferences(verbose, ds, prior_voting, posterior_voting));
        return ret;

	}
	
	/**
	 * @param categories
	 * @param unlabeled
	 * @param gold
	 * @param costs
	 * @param iterations
	 * @return
	 */
	public static String validate(String categories, String unlabeled, String gold, String costs, String iterations)  {
		StringBuilder result = new StringBuilder("");
		if (isEmpty(categories)) {
			String name = "categories";
			result.append("ERROR("+name+"): Empty field\n");
		}
		if (isEmpty(unlabeled)) {
			String name = "unlabeled";
			result.append("ERROR("+name+"): Empty field\n");
		}
		if (isEmpty(gold)) {
			String name = "gold";
			result.append("ERROR("+name+"): Empty field\n");
		}
		if (isEmpty(costs)) {
			String name = "costs";
			result.append("ERROR("+name+"): Empty field\n");
		}
		if (isEmpty(iterations)) {
			String name = "iterations";
			result.append("ERROR("+name+"): Empty field\n");
		}
		if (result.length()>0) return result.toString();
		
		try {
			categorySet = getCategories(categories);
		} catch (Exception e) {
			result = new StringBuilder("categorySetError="+e.getMessage()+"\n");
		}
		try {
			assignedLabelSet = getUnlabeled(unlabeled);
		} catch (Exception e) {
			result.append("unlabeledSetError="+e.getMessage()+"\n");
		}
		try {
			correctLabelSet = getGoldLabels(gold);
		} catch (Exception e) {
			result.append("correctLabelSetError="+e.getMessage()+"\n");
		}
		try {
			costSet = getCosts(costs);
		} catch (Exception e) {
			result.append("costSetError="+e.getMessage()+"\n");
		}
		try {
			iterationsInt = Integer.parseInt(iterations);
			if (iterationsInt<1) result.append("costSetError="+iterationsInt+" is less than 0\n");
		} catch(NumberFormatException e) {
			result.append("costSetError="+e.getMessage()+"\n");
		}
		return result.toString();
	}
	
	/**
	 * @param categories
	 * @return
	 * @throws Exception
	 */
	private static Set<Category> getCategories(final String categories) throws Exception {
		final String errorMsg = "wrong category set";
		Set<Category> categorySet = new HashSet<Category>();
		String[] rawRows = categories.split("\n");
		if (rawRows==null || rawRows.length<1) throw new Exception(errorMsg);
		for (String row: rawRows) {
			String[] words = row.split("\t");
            if (words.length == 1) {
                Category category = new Category(row);
                categorySet.add(category);
            } else if (words.length == 2) {
                String name = words[0];
                Double prior = new Double(words[1]);
                Category category = new Category(name);
                category.setPrior(prior);
                categorySet.add(category);
            } else 
            	throw new Exception(errorMsg);
		}
		return categorySet;
	}
	
	/**
	 * @param unlabeled
	 * @return
	 * @throws Exception 
	 */
	private static Set<AssignedLabel> getUnlabeled(final String unlabeled) throws Exception {
		final String errorMsg = "wrong assigned label (unlabeled) set";
		Set<AssignedLabel> assignedLabelSet = new HashSet<AssignedLabel>();
		String[] rawRows = unlabeled.split("\n");
		if (rawRows==null || rawRows.length<1) throw new Exception(errorMsg);
		for (String row: rawRows) {
			String[] words = row.split("\t");
			if (words.length != 3) {
				throw new Exception(errorMsg);
			}
			String workername = words[0];
			String objectname = words[1];
			String categoryname = normalizeWord(words[2]);
			AssignedLabel al = new AssignedLabel(workername, objectname,
					categoryname);
			assignedLabelSet.add(al);
		}
		return assignedLabelSet;
	}
	/**
	 * @param golds
	 * @return
	 * @throws Exception
	 */
	private static Set<CorrectLabel> getGoldLabels(final String golds) throws Exception {
		final String errorMsg = "wrong correct label (gold) set";
		Set<CorrectLabel> goldLabels = new HashSet<CorrectLabel>();
		String[] rawRows = golds.split("\n");
		if (rawRows==null || rawRows.length<1) throw new Exception(errorMsg);
		for (String row: rawRows) {
			String[] words = row.split("\t");
			if (words.length != 2) {
				throw new Exception(errorMsg);
			}
            String objectname = words[0];
            String categoryname = normalizeWord(words[1]);

            CorrectLabel cl = new CorrectLabel(objectname, categoryname);
            goldLabels.add(cl);
		}
		return goldLabels;
	}
	/**
	 * @param costs
	 * @return
	 * @throws Exception 
	 */
	private static Set<MisclassificationCost> getCosts(final String costs) throws Exception {
		final String errorMsg = "wrong cost set";
		Set<MisclassificationCost> costSet = new HashSet<MisclassificationCost>();
		String[] rawRows = costs.split("\n");
		if (rawRows==null || rawRows.length<1) throw new Exception(errorMsg);
		for (String row: rawRows) {
			String[] words = row.split("\t");
			if (words.length != 3) {
				throw new Exception(errorMsg);
			}
            String from = words[0];
            String to = words[1];
            Double cost = Double.parseDouble(words[2]);

            MisclassificationCost mcc = new MisclassificationCost(from, to, cost);
            costSet.add(mcc);
		}
		return costSet;
	}
	
    /**
     * @param verbose
     * @param ds
     */
	private static StringBuffer saveWorkerQuality(final boolean verbose, final DawidSkene ds) {
		StringBuffer ret = new StringBuffer("");
        // Save the estimated quality characteristics for each worker
        ret.append("\nEstimating worker quality");
        boolean detailed = false;
        String summary_report = ds.printAllWorkerScores(detailed);
        detailed = true;
        String detailed_report = ds.printAllWorkerScores(detailed);
        if (verbose) {
            ret.append("\n=======WORKER QUALITY STATISTICS(SUMMARY)=======");
            ret.append("\n"+summary_report);
            ret.append("\n=======================================");
        }
        ret.append("\n=======WORKER QUALITY STATISTICS(DETAILED)=======");
        ret.append("\n"+detailed_report);
        ret.append("\n=======================================");
        return ret;
    }
    
    /**
     * @param verbose
     * @param ds
     */
    private static StringBuffer saveObjectResults(final boolean verbose, final DawidSkene ds) {
    	StringBuffer ret = new StringBuffer("");
        // Save the probability that an object belongs to each class
        String objectProbs = ds.printObjectClassProbabilities(0.0);
        if (verbose) {
        	ret.append("\n=======CATEGORY PROBABILITIES========");
        	ret.append("\n"+objectProbs);
        	ret.append("\n=====================================");
        }
        return ret;
    }
    /**
     * @param verbose
     * @param ds
     */
    private static StringBuffer saveCategoryPriors(final boolean verbose, final DawidSkene ds) {
    	StringBuffer ret = new StringBuffer("");
        // Save the probability that an object belongs to each class
        String priors = ds.printPriors();
        if (verbose) {
        	ret.append("\n=======PRIOR PROBABILITIES========");
        	ret.append("\n"+priors);
        	ret.append("\n==================================");
        }
        return ret;
    }
    /**
     * @param verbose
     * @param ds
     * @return
     */
    private static StringBuffer saveDawidSkeneVote(final boolean verbose,
           final DawidSkene ds) {
    	StringBuffer ret = new StringBuffer("");
        // Save the vote after the D&S estimation
        String dawidskene = ds.printVote();
        if (verbose) {
        	ret.append("\n=======DAWID&SKENE RESULTS========");
        	ret.append("\n"+dawidskene);
        	ret.append("\n==================================");
        }
        return ret;
    }

    /**
     * @param verbose
     * @param ds
     * @param prior_voting
     * @param posterior_voting
     * @return
     */
    private static StringBuffer saveDifferences(final boolean verbose, final DawidSkene ds,
            final Map<String, String> prior_voting,
            final Map<String, String> posterior_voting) {
    	StringBuffer ret = new StringBuffer("");
        String differences = ds.printDiffVote(prior_voting, posterior_voting);
        if (verbose) {
        	ret.append("\n=======DIFFERENCES WITH MAJORITY VOTE========");
        	ret.append("\n"+differences);
        	ret.append("\n=============================================");
        }
        return ret;
    }
    
    /**
     * @param verbose
     * @param ds
     * @return
     */
    private static StringBuffer saveMajorityVote(final boolean verbose,
            final DawidSkene ds) {
    	StringBuffer ret = new StringBuffer("");
        // Save the majority vote before the D&S estimation
        String majority = ds.printVote();
        if (verbose) {
        	ret.append("\n=======NAIVE MAJORITY VOTE========");
        	ret.append("\n"+majority);
        	ret.append("\n==================================");
        }
        return ret;
    }
 
    /**
     * @param word
     * @return
     */
    private static String normalizeWord(final String word) {
    	String normalized = word.replace("\r", "");
    	normalized = normalized.replace("\t", "");
    	normalized = normalized.replace("\\r", "");
//    	normalized = normalized.trim();
    	return normalized;
    }
}
