/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.datascience.gal.scripts;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.datascience.gal.AssignedLabel;
import com.datascience.gal.Category;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.MisclassificationCost;
import com.datascience.gal.service.DawidSkeneClient;
import com.datascience.gal.service.JSONUtils;
import com.datascience.utils.Utils;

/**
 * TODO: vote diff with incremental duplicates the other main function using the
 * service architecture.
 * 
 * @author josh
 * 
 */
public class MainClient {
    private static Options cli_options;
    static {
        cli_options = new Options();
        cli_options
                .addOption(
                        "c",
                        "categories",
                        true,
                        "a text file and contains the list of categories used to annotate the objects.\n"
                                + "It contains one category per line.\n"
                                + "The file can also be used to define the prior values for the different categories, instead of letting the priors be defined by the data.\n"
                                + "In that case, it becomes a tab-separated file and each line has the form <category><tab><prior>\n");
        cli_options
                .addOption(
                        "g",
                        "gold",
                        true,
                        "a tab-separated text file.\n"
                                + "Each line has the form <objectid><tab><assigned_label>\n"
                                + "and records the correct labels for whatever objects we have them.\n");
        cli_options
                .addOption(
                        "C",
                        "costs",
                        true,
                        "is a tab-separated text file.\n"
                                + "Each line has the form <from_class><tab><to_class><tab><classification_cost>\n"
                                + "and records the classification cost of classifying an object that\n"
                                + "belongs to the `from_class` into the `to_class`.\n");
        cli_options
                .addOption(
                        "l",
                        "labels",
                        true,
                        "a tab-separated text file.\n"
                                + "Each line has the form <workerid><tab><objectid><tab><assigned_label>\n"
                                + "and records the correct labels for whatever objects we have them.\n");
        cli_options
                .addOption("i", "iterations", true,
                        "the number of times to run the algorithm. Even a value of 1 works well.");
        cli_options
                .addOption("I", "incremental", false,
                        "use incremental dawid skene algorithm - update on each observation of data");
    }

    private static void printHelp() {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(MainClient.class.toString(), cli_options);
    }

    public static void main(String[] args) throws ParseException {

        DawidSkeneClient dsclient = new DawidSkeneClient();

        String categoriesfile = null;
        String inputfile = null;
        String correctfile = null;
        String costfile = null;
        int iterations = 1;
        boolean incremental = false;

        CommandLineParser parser = new GnuParser();
        CommandLine line = parser.parse(cli_options, args);

        if (!line.hasOption("c") || !line.hasOption("l")
                || !line.hasOption("g") || !line.hasOption("C")) {
            printHelp();
            System.exit(1);
        }

        categoriesfile = line.getOptionValue("c");
        inputfile = line.getOptionValue("l");

        if (line.hasOption("g"))
            correctfile = line.getOptionValue("g");
        if (line.hasOption("C"))
            costfile = line.getOptionValue("C");
        if (line.hasOption("i"))
            iterations = Integer.parseInt(line.getOptionValue("i"));
        if (line.hasOption("I"))
            incremental = true;

        // see if the thing is awake
        System.out.println(dsclient.isAlive() ? "pinged alive"
                : "problem with dsclient");
        // hard reset
        System.out.println(dsclient.deleteDS(0) ? "deleted ds 0"
                : "failed to delete ds 0");

        Collection<Category> categories = loadCategoriesFromFile(categoriesfile);
        if (categories.size() < 2) {
            System.err.println("invalid category input");
            System.exit(1);
        }

        System.out.println("sending to service: "
                + JSONUtils.toJson(categories));

        System.out
                .println("response: "
                        + (dsclient.initializeDS(0, categories, incremental) ? "success"
                                : "failure"));

        // check the serialization of the json ds object
        DawidSkene ds = dsclient.getDawidSkene(0);
        System.out.println(ds);

        Collection<MisclassificationCost> costs = loadMisclassificationCosts(costfile);

        System.out
                .println("response: "
                        + (dsclient.addMisclassificationCosts(0, costs) ? "successfully added costs"
                                : "unable to add costs") + " - "
                        + JSONUtils.toJson(costs));

        Collection<AssignedLabel> labels = loadAssignedLabels(inputfile);

        System.out
                .println("response: "
                        + (dsclient.addAssignedLabels(0, labels) ? "successfully added labels"
                                : "unable to add labels") + " - "
                        + JSONUtils.toJson(labels));

        Collection<CorrectLabel> gold = loadGoldLabels(correctfile);
        System.out
                .println("response: "
                        + (dsclient.addCorrectLabels(0, gold) ? "successfully added labels"
                                : "unable to add labels") + " - "
                        + JSONUtils.toJson(gold));

        Map<String, String> preVotes = dsclient.computeMajorityVotes(0);

        ds = dsclient.getDawidSkene(0);

        System.out.println(ds);
        if (!incremental)
            System.out
                    .println(dsclient.iterateBlocking(0, iterations) ? "successfully iterated"
                            : "failed ds iterations");

        ds = dsclient.getDawidSkene(0);
        System.out.println(ds);

        System.out.println(ds.printAllWorkerScores(false));
        System.out.println(ds.printObjectClassProbabilities(0));
        System.out.println(ds.printPriors());

        Map<String, String> postVotes = dsclient.computeMajorityVotes(0);

        System.out.println("note- diff doesnt work with incremental\n"
                + printDiffVote(preVotes, postVotes));

        System.out.println(JSONUtils.toJson(ds.getMajorityVote()));
        System.out.println(JSONUtils.toJson(ds.getObjectProbs()));

        Collection<String> objectNames = new HashSet<String>();
        String name = null;
        for (AssignedLabel label : labels) {
            objectNames.add(label.getObjectName());
            name = label.getObjectName();
        }

        System.out.println(JSONUtils.toJson(ds.getMajorityVote(objectNames)));
        System.out.println(JSONUtils.toJson(ds.getObjectProbs(objectNames)));

        System.out.println(JSONUtils.toJson(ds.getMajorityVote(name)));
        System.out.println(JSONUtils.toJson(ds.getObjectProbs(name)));

        boolean contains;
        contains = dsclient.hasDSObject(0);
        System.out.println(contains ? "has 0" : "dont have 0");
        contains = dsclient.hasDSObject(1);
        System.out.println(contains ? "has 1" : "dont have 1");
        System.out.println("deleting 0");
        dsclient.deleteDS(0);
        contains = dsclient.hasDSObject(0);
        System.out.println(contains ? "has 0" : "dont have 0");
    }

    public static String printDiffVote(Map<String, String> prior_voting,
            Map<String, String> posterior_voting) {

        StringBuffer sb = new StringBuffer();

        for (String obj : (new TreeSet<String>(prior_voting.keySet()))) {
            String prior_vote = prior_voting.get(obj);
            String posterior_vote = posterior_voting.get(obj);

            if (prior_vote.equals(posterior_vote)) {
                sb.append("SAME\t" + obj + "\t" + prior_vote);
            } else {
                sb.append("DIFF\t" + obj + "\t" + prior_vote + "->"
                        + posterior_vote);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private static Collection<CorrectLabel> loadGoldLabels(String correctfile) {
        Collection<CorrectLabel> out = new HashSet<CorrectLabel>();
        String[] lines = Utils.getFile(correctfile).split("\n");
        for (String line : lines) {
            String[] parts = line.split("\t");
            if (parts.length < 2) {
                System.err.println("invalid correct label: " + line);
                continue;
            }
            out.add(new CorrectLabel(parts[0], parts[1]));
        }

        return out;
    }

    private static Collection<AssignedLabel> loadAssignedLabels(String inputfile) {
        Collection<AssignedLabel> out = new HashSet<AssignedLabel>();
        String[] lines = Utils.getFile(inputfile).split("\n");
        for (String line : lines) {
            String[] parts = line.split("\t");
            if (parts.length < 3) {
                System.err.println("invalid worker assigned label: " + line);
                continue;
            }
            out.add(new AssignedLabel(parts[0], parts[1], parts[2]));
        }
        return out;
    }

    private static Collection<MisclassificationCost> loadMisclassificationCosts(
            String costfile) {
        Collection<MisclassificationCost> out = new HashSet<MisclassificationCost>();
        String[] lines_cost = Utils.getFile(costfile).split("\n");
        for (String line : lines_cost) {
            String[] parts = line.split("\t");
            if (parts.length < 3) {
                System.err.println("invalid cost input: " + line);
                continue;
            }
            out.add(new MisclassificationCost(parts[0], parts[1], Double
                    .parseDouble(parts[2])));
        }
        return out;
    }

    private static Collection<Category> loadCategoriesFromFile(
            String categoriesfile) {
        Collection<Category> out = new HashSet<Category>();
        String[] lines_categories = Utils.getFile(categoriesfile).split("\n");
        for (String line : lines_categories) {
            String[] parts = line.split("\t");
            if (parts.length < 1)
                continue;
            Category cat = new Category(parts[0]);
            if (parts.length > 1)
                cat.setPrior(Double.parseDouble(parts[1]));
            out.add(cat);
        }
        return out;
    }

}
