/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.datascience.core.storages;

import com.datascience.core.Job;
import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.NominalData;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.results.DatumResult;
import com.datascience.core.results.Results;
import com.datascience.core.results.WorkerResult;
import com.datascience.mv.BatchMV;
import com.datascience.service.GSONSerializer;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author dana
 */
public class CachedAndDBJobStorageTests {
    IJobStorage storage;
    protected ArrayList<String> categories;
    protected ArrayList<Worker<String>> workers;
    protected ArrayList<LObject<String>> objects;
    protected ArrayList<LObject<String>> goldObjects;
    protected ArrayList<AssignedLabel<String>> assigns;
    int nWorkers = 3, nObjects = 4, nGold = 1;

    @Before
    public void setUp() {
        categories = new ArrayList<String>();
        categories.add("Cat1");
        categories.add("Cat2");
        
        int i;
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
            gold.setGoldLabel("Cat1");
            goldObjects.add(gold);
        }
        
        objects.addAll(goldObjects);
        
        int nAssigns = nWorkers * (nObjects + nGold);
        assigns = new ArrayList<AssignedLabel<String>>();
        for (i = 0; i < nAssigns; i++) {
            assigns.add(new AssignedLabel<String>(workers.get(i % nWorkers),
                    objects.get(i % (nObjects + nGold)),
                    categories.get(i % categories.size())));
        }
    }

    protected void fillNominalData(NominalData data) {
        for (LObject<String> gold : goldObjects) {
            data.addObject(gold);
        }
        for (AssignedLabel<String> assign : assigns) {
            data.addAssign(assign);
        }
    }

    @Test
    public void testMixedStorages() throws Exception {
        BatchMV mv = new BatchMV();
	NominalProject np = new NominalProject(mv);
	np.initializeCategories(categories, null, null);
	NominalData nd = np.getData();
	Results<String, DatumResult, WorkerResult> results = np.getResults();
	mv.setData(nd);
	mv.setResults(results);
	fillNominalData(nd);
	mv.compute();
       
        CachedJobStorage cachedJobStorage = new CachedJobStorage(storage, 1);
        Job job1 = new Job(np, "job1");
        Job job2 = new Job(np, "job2");
        cachedJobStorage.add(job1);
        cachedJobStorage.add(job2);
        //at this point job1 should be removed from cache and added to db
        
        DBJobStorage dbJobStorage = new DBJobStorage("root", "root", "Troia", "localhost", new GSONSerializer());
        Job<NominalProject> dbJob = dbJobStorage.get("job1");
        NominalData dbJobData = dbJob.getProject().getData();
        
        


    }
}
