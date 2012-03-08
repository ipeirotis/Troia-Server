/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.datascience.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.datascience.gal.AssignedLabel;
import com.datascience.gal.BatchDawidSkene;
import com.datascience.gal.Category;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.IncrementalDawidSkene;

public class TaskSimulator {
    public static final Random RAND = new Random();
    // for chinese restaurant process
    private double alpha;
    private double theta;
    private Map<String, Double> categoriesWithPriors;
    private Map<String, Double> workersWithErrors;

    private Map<Integer, Map<String, String>> objectToLabelSet;
    private Map<Integer, String> objectToLabel;
    private Set<Integer> unlabeled;
    private Set<Integer> goldUnlabeled;
    private int iteration;

    public TaskSimulator(double alpha, double theta,
            Map<String, Double> categoriesWithPriors,
            Map<String, Double> workersWithErrors) {
        this.alpha = alpha;
        this.theta = theta;
        this.categoriesWithPriors = categoriesWithPriors;
        this.workersWithErrors = workersWithErrors;

        objectToLabelSet = new HashMap<Integer, Map<String, String>>();
        objectToLabel = new HashMap<Integer, String>();
        unlabeled = new HashSet<Integer>();
        goldUnlabeled = new HashSet<Integer>();
        iteration = 0;
    }

    /**
     * 
     * @return an artificial assignedlabel
     */
    public AssignedLabel drawAssignedLabel() {

        // 1. decided to either label a new example (building it first) or to
        // put a new label on an existing example
        Set<Integer> unlabeledExamples = gatherUnlabeled();
        Map<Integer, Double> probs = new HashMap<Integer, Double>(
                unlabeledExamples.size());

        double tot = 0;
        for (Integer ex : unlabeledExamples) {
            double ct = objectToLabelSet.get(ex).size();
            tot += ct;
            probs.put(ex, ct - alpha);
        }

        double draw = RAND.nextDouble() * (tot + theta);
        int choice = drawFromProbs(probs, draw);

        // 2. pick a worker able to label the example
        Set<String> unusedWorkers = new HashSet<String>(
                workersWithErrors.keySet());
        unusedWorkers.removeAll(objectToLabelSet.get(choice).keySet());

        String worker = null;
        int checkI = 0, val = RAND.nextInt(unusedWorkers.size());
        for (String tWorker : unusedWorkers) {
            if (checkI == val) {
                worker = tWorker;
                break;
            }
            checkI++;
        }
        // 3. draw a label according to that worker's error rate
        String label = objectToLabel.get(choice);
        double prob = RAND.nextDouble();
        if (prob < workersWithErrors.get(worker)) {
            // erronious label, choose which mistake to make
            Set<String> wrongLabels = new HashSet<String>(
                    categoriesWithPriors.keySet());
            wrongLabels.remove(label);
            // TODO: draw wrong labels according to priors instead of uniformly
            checkI = 0;
            val = RAND.nextInt(wrongLabels.size());

            for (String wLabel : wrongLabels) {
                if (checkI == val) {
                    label = wLabel;
                    break;
                }
                checkI++;
            }
        }
        objectToLabelSet.get(choice).put(worker, label);
        if (objectToLabelSet.get(choice).size() == workersWithErrors.size())
            unlabeled.remove(choice);
        return new AssignedLabel(worker, "" + choice, label);
    }

    private Integer drawFromProbs(Map<Integer, Double> probs, double draw) {

        for (Integer ex : probs.keySet()) {
            double p = probs.get(ex) - alpha;
            if (draw - p <= 0) {
                return ex;
            } else {
                draw -= p;
            }
        }
        return buildNewExample();
    }

    private Integer buildNewExample() {
        // draw label according to priors
        double draw = RAND.nextDouble();
        String category = null;
        for (String ex_category : categoriesWithPriors.keySet()) {
            double p = categoriesWithPriors.get(ex_category);
            if (draw - p <= 0) { // worry about epsilon issues here?
                category = ex_category;
                break;
            }
            draw -= p;
        }
        objectToLabel.put(iteration, category);
        unlabeled.add(iteration);
        goldUnlabeled.add(iteration);
        objectToLabelSet.put(iteration, new HashMap<String, String>());
        return iteration++;
    }

    /**
     * TODO: is this a dumb way to do a CRP?
     * 
     * @return
     */
    private Set<Integer> gatherUnlabeled() {
        return unlabeled;

    }

    // TODO: this makes it possible to get > 1 gold label / example. need more
    // book keeping.
    public CorrectLabel drawCorrectLabel() {
        List<Integer> examples = new LinkedList<Integer>(goldUnlabeled);
        examples.add(iteration);
        Collections.shuffle(examples);
        int choice = examples.get(0);

        if (choice == iteration) {
            choice = buildNewExample();
        }
        goldUnlabeled.remove(choice);
        return new CorrectLabel("" + choice, objectToLabel.get(choice));
    }

    public String trueLabelForExample(String name) {
        return objectToLabel.get(name);
    }

    public Set<Integer> objects() {
        return objectToLabel.keySet();
    }

    public Map<Integer, String> labeledObjects() {
        return objectToLabel;
    }

    /**
     * rudimentary test method
     * 
     * @param args
     */
    public static void main(String[] args) {
        int categories = Integer.parseInt(args[0]);
        int workers = Integer.parseInt(args[1]);
        int examples = Integer.parseInt(args[2]);

        Map<String, Double> workersWithErrors = new HashMap<String, Double>();

        for (int i = 0; i < workers; i++) {
            String name = "worker_" + i;
            double error = i == 0 ? 0 : RAND.nextDouble();
            workersWithErrors.put(name, error);
        }

        Map<String, Double> categoriesWithPriors = new HashMap<String, Double>();

        for (int i = 0; i < categories; i++) {
            String name = "category_" + i;
            double prior = 1. / (double) categories;
            categoriesWithPriors.put(name, prior);
        }
        Set<Category> cats = new HashSet<Category>();
        for (String cat : categoriesWithPriors.keySet()) {
            cats.add(new Category(cat));
        }
        DawidSkene ds = new IncrementalDawidSkene("test", cats);
        DawidSkene ds2 = new BatchDawidSkene("test", cats);

        TaskSimulator sim = new TaskSimulator(.2, 10, categoriesWithPriors,
                workersWithErrors);
        for (int i = 0; i < examples; i++) {
            AssignedLabel al = sim.drawAssignedLabel();
            ds.addAssignedLabel(al);
            ds2.addAssignedLabel(al);
            if (i % 1000 == 0)
                System.out.println(i + "\t" + al);
        }
        System.out.println(ds.printAllWorkerScores(false));
        ds2.estimate(5);
        System.out.println("+++++");
        System.out.println(ds2.printAllWorkerScores(false));
    }

}
