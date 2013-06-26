package com.datascience.core.stats;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.CategoryValue;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.results.WorkerResult;
import com.datascience.datastoring.datamodels.full.MemoryJobStorage;
import com.datascience.gal.BatchDawidSkene;
import com.datascience.utils.CostMatrix;
import java.util.Arrays;
import java.util.Collection;

import com.google.common.collect.Lists;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author dana
 */
public class QualitySensitivePaymentsTest {

    public static NominalProject setUpNominalProject(Collection<String> categories, Collection<CategoryValue> categoryPriors, CostMatrix<String> costMatrix) {
        BatchDawidSkene bds = new BatchDawidSkene();
        MemoryJobStorage js = new MemoryJobStorage();
        NominalProject project = new NominalProject(bds, js.getNominalData("testId"), js.getNominalResults("testId", categories));
        project.initializeCategories(categories, categoryPriors, costMatrix);
        return project;
    }

    protected Worker worker(int i) {
        return new Worker("worker" + i);
    }

    protected AssignedLabel<String> assign(int i, LObject<String> obj, String label) {
        return new AssignedLabel<String>(worker(i), obj, label);
    }

    @Test
    public void testZeroWorkerQuality() {
        Collection<String> categories = Arrays.asList(new String[]{"cat1", "cat2"});
        NominalProject project = setUpNominalProject(categories, null, null);
        INominalData data = project.getData();
        LObject<String> object1 = data.getOrCreateObject("object1");
        LObject<String> object2 = data.getOrCreateObject("object2");

        LObject<String> goldObj1 = new LObject<String>("object1");
        goldObj1.setGoldLabel("cat1");
        LObject<String> goldObj2 = new LObject<String>("object2");
        goldObj2.setGoldLabel("cat2");
        data.addObject(goldObj1);
        data.addObject(goldObj2);

        data.addAssign(assign(1, object1, "cat2"));
        data.addAssign(assign(1, object2, "cat1"));
        data.addAssign(assign(2, object1, "cat1"));
        data.addAssign(assign(2, object2, "cat1"));
        data.addAssign(assign(3, object1, "cat1"));
        data.addAssign(assign(3, object2, "cat2"));

        project.setData(data);
        project.getAlgorithm().compute();
        QualitySensitivePaymentsCalculator wspq;
        for (Worker w : project.getData().getWorkers()) {
            wspq = new QSPCalculators.Linear(project, w);
            wspq.getWorkerWage(1.0, 0.01);
            wspq = new QSPCalculators.RegressionBased(project, w);
            wspq.getWorkerWage(1.0, 0.01);
        }
    }

    @Test
    public void testSpammerWage() {
        double tolerance = 0.0;
        Collection<String> categories = Arrays.asList(new String[]{"A", "B"});
        Collection<CategoryValue> categoryPriors = Arrays.asList(new CategoryValue("A", 0.5), new CategoryValue("B", 0.5));
        CostMatrix<String> costMatrix = new CostMatrix<String>();
        costMatrix.add("A", "A", 0.0);
        costMatrix.add("A", "B", 1.0);
        costMatrix.add("B", "A", 1.0);
        costMatrix.add("B", "B", 0.0);
        NominalProject project = setUpNominalProject(categories, categoryPriors, costMatrix);

        WorkerResult wr;
        Worker wGood = new Worker("wGood");
        wr = project.getResults().getOrCreateWorkerResult(wGood);
        wr.empty();
        wr.addError("A", "B", 0.);
        wr.addError("A", "A", 1.);
        wr.addError("B", "A", 0.);
        wr.addError("B", "B", 1.);
        project.getResults().addWorkerResult(wGood, wr);

        Worker wSpamm = new Worker("wSpamm");
        wr = project.getResults().getOrCreateWorkerResult(wSpamm);
        wr.empty();
        wr.addError("A", "B", 1.);
        wr.addError("A", "A", 0.);
        wr.addError("B", "A", 0.);
        wr.addError("B", "B", 1.);
        project.getResults().addWorkerResult(wSpamm, wr);

        Worker wOpos = new Worker("wOpos");
        wr = project.getResults().getOrCreateWorkerResult(wOpos);
        wr.empty();
        wr.addError("A", "B", 1.);
        wr.addError("A", "A", 0.);
        wr.addError("B", "A", 1.);
        wr.addError("B", "B", 0.);
        project.getResults().addWorkerResult(wOpos, wr);

        QualitySensitivePaymentsCalculator wspq;
        for (Worker w : Lists.newArrayList(wGood, wSpamm, wOpos)) {
            wspq = new QSPCalculators.Linear(project, w);
            double workerWage = wspq.getWorkerWage(1.0, 0.01);
            if (w.getName().equals("wSpamm")) {
                assertEquals(0.0, workerWage, tolerance);
            } 
            else {
                assertEquals(1.0, workerWage, tolerance);
            }
        }
        // This one doesn't work
//            wspq = new QSPCalculators.RegressionBased(project, w);
//            assertNotEquals(Double.NaN, wspq.getWorkerWage(1.0, 0.01));
    }
}
