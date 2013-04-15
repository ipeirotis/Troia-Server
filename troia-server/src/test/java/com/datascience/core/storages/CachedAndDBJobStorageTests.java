/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.datascience.core.storages;

import com.datascience.core.Job;
import com.datascience.core.JobFactory;
import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.NominalData;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.results.DatumResult;
import com.datascience.core.results.Results;
import com.datascience.core.results.WorkerResult;
import com.datascience.service.GSONSerializer;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import com.datascience.scheduler.Constants;

import com.google.gson.JsonArray;
import com.google.gson.JsonPrimitive;
import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author dana
 */
public class CachedAndDBJobStorageTests {

    protected String dbUser;
    protected String dbPassword;
    protected String dbName;
    protected String dbUrl;

    private void getDBProperties() {
        Properties props = new Properties();
        try {
            File dir = new File(".");
            props.load(new FileInputStream(dir.getCanonicalPath() + dir.separator + "src" + dir.separator + "main" + dir.separator + "resources" + dir.separator + "troia.properties"));
            dbUser = props.getProperty("DB_USER");
            dbPassword = props.getProperty("DB_PASSWORD");
            dbName = props.getProperty("DB_NAME");
            dbUrl = props.getProperty("DB_URL");
        } catch (Exception ex) {
            Logger.getLogger(CachedAndDBJobStorageTests.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private NominalData initializeNominalData() {
        ArrayList<Worker<String>> workers;
        ArrayList<LObject<String>> objects;
        ArrayList<LObject<String>> goldObjects;
        ArrayList<AssignedLabel<String>> assigns;
        Set<String> categories;
        NominalData nominalData = new NominalData();
        int nWorkers = 2;
        int nObjects = 4;
        int nGold = 2;
        int i;

        categories = new HashSet<String>();
        categories.add("cat1");
        categories.add("cat2");

        nominalData.setCategories(categories);

        workers = new ArrayList<Worker<String>>();
        for (i = 0; i < nWorkers; i++) {
            workers.add(new Worker<String>("worker" + i));
        }

        objects = new ArrayList<LObject<String>>();
        for (i = 0; i < nObjects; i++) {
            objects.add(new LObject<String>("object" + i));
        }

        goldObjects = new ArrayList<LObject<String>>();
        for (i = 0; i < nGold; i++) {
            LObject<String> gold = new LObject<String>("gObject" + i);
            gold.setGoldLabel(categories.iterator().next().toString());
            goldObjects.add(gold);
        }

        objects.addAll(goldObjects);

        int nAssigns = nWorkers * (nObjects + nGold);
        assigns = new ArrayList<AssignedLabel<String>>();
        for (i = 0; i < nAssigns; i++) {
            assigns.add(new AssignedLabel<String>(workers.get(i % nWorkers),
                    objects.get(i % (nObjects + nGold)),
                    categories.iterator().next().toString()));
        }

        for (LObject<String> gold : goldObjects) {
            nominalData.addObject(gold);
        }

        for (AssignedLabel<String> assign : assigns) {
            nominalData.addAssign(assign);
        }
        return nominalData;
    }

    @Before
    public void setUp() {
        getDBProperties();
    }

    private JsonArray createCategoriesJsonArray(Set<String> categories) {
        JsonArray cat = new JsonArray();
        Iterator<String> iterator = categories.iterator();
        while (iterator.hasNext()) {
            String category = iterator.next();
            cat.add(new JsonPrimitive(category));
        }
        return cat;
    }

    private void checkResults(Results expectedResults, Results actualResults) {
        
        Map expectedDatumResults = expectedResults.getDatumResults();
        for (Iterator it = expectedDatumResults.entrySet().iterator(); it.hasNext();) {
            Object obj  = it.next();
            System.out.println(obj);
          }                   
    }

    @Test
    public void testMixedStorages() throws Exception {
        JobFactory jobFactory = new JobFactory(new GSONSerializer());
        JsonObject jo = new JsonObject();
        NominalData nominalData = initializeNominalData();
        jo.addProperty("algorithm", "BDS");
        jo.add("categories", createCategoriesJsonArray(nominalData.getCategories()));
        jo.addProperty(Constants.SCHEDULER, Constants.SCHEDULER_NORMAL);

        Job job1 = jobFactory.createNominalJob(jo, "job1");
        job1.getProject().setData(nominalData);
        job1.getProject().getAlgorithm().compute();
        String job1Kind = job1.getProject().getKind();
        Results job1Results = job1.getProject().getResults();
        //Assert.assertFalse(job1Results.getDatumResults().isEmpty());
        
        Job job2 = jobFactory.createNominalJob(jo, "job2");
        job2.getProject().setData(nominalData);
        job2.getProject().getAlgorithm().compute();

        IJobStorage dbJobStorage = new DBJobStorage(dbUser, dbPassword, dbName, dbUrl, new GSONSerializer());
        CachedJobStorage cachedJobStorage = new CachedJobStorage(dbJobStorage, 1);

        cachedJobStorage.add(job1);
        cachedJobStorage.add(job2);
        //at this point job1 should be removed from cache and added to db

        //check that the job received from the db storage has the same data as the original one
        Job<NominalProject> dbJob = dbJobStorage.get("job1");
        Assert.assertEquals("job1", dbJob.getId());

        NominalProject dbNominalProject = dbJob.getProject();
        Assert.assertEquals(job1Kind, dbNominalProject.getKind());
        //checkResults(job1Results, dbNominalProject.getAlgorithm().getResults());

        Data dbJobData = dbNominalProject.getData();
        Assert.assertEquals(nominalData.getAssigns(), dbJobData.getAssigns());
        Assert.assertEquals(nominalData.getGoldObjects(), dbJobData.getGoldObjects());
        Assert.assertEquals(nominalData.getEvaluationObjects(), dbJobData.getEvaluationObjects());
        Assert.assertEquals(nominalData.getObjects(), dbJobData.getObjects());
        Assert.assertEquals(nominalData.getWorkers(), dbJobData.getWorkers());
    }
}
