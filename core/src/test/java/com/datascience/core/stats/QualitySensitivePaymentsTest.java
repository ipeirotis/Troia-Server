package com.datascience.core.stats;

import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.INominalData;
import com.datascience.core.nominal.NominalProject;
import com.datascience.datastoring.storages.MemoryJobStorage;
import com.datascience.gal.BatchDawidSkene;
import static com.datascience.scheduler.CostBasedPriorityCalculatorTest.setUpNominalProject;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Test;

/**
 *
 * @author dana
 */
public class QualitySensitivePaymentsTest {

    public static NominalProject setUpNominalProject(){
		BatchDawidSkene bds = new BatchDawidSkene();
		MemoryJobStorage js = new MemoryJobStorage();
		Collection<String> categories = Arrays.asList(
				new String[]{"cat1","cat2"});
		NominalProject project = new NominalProject(bds, js.getNominalData("testId"), js.getNominalResults("testId", categories));
		project.initializeCategories(categories, null, null);
		return project;
	}

	protected Worker<String> worker(int i){
		return new Worker<String>("worker" + i);
	}

	protected AssignedLabel<String> assign(int i, LObject<String> obj, String label){
		return new AssignedLabel<String>(worker(i), obj, label);
	}

	@Test
	public void testZeroWorkerQuality(){
                NominalProject project = setUpNominalProject();
		INominalData data = project.getData();
		LObject<String> object1 = data.getOrCreateObject("object1");
		LObject<String> object2 = data.getOrCreateObject("object2");

		data.addAssign(assign(1, object1, "cat2"));
		data.addAssign(assign(1, object2, "cat1"));
                data.addAssign(assign(2, object1, "cat1"));
                data.addAssign(assign(2, object2, "cat1"));
                data.addAssign(assign(3, object1, "cat1"));
                data.addAssign(assign(3, object2, "cat2"));
                
        	LObject<String> goldObj1 = new LObject<String>("object1");
            	goldObj1.setGoldLabel("cat1");
                LObject<String> goldObj2 = new LObject<String>("object2");
            	goldObj2.setGoldLabel("cat2");
                data.addObject(goldObj1);
                data.addObject(goldObj2);
                project.setData(data);
                project.getAlgorithm().compute();
                
                for (Worker<String> w : project.getData().getWorkers()){
                    QualitySensitivePaymentsCalculator wspq = new QualitySensitivePaymentsCalculator(project, w);
                    Double wage = wspq.getWorkerWage(1.0, 0.01);  
                }
		
		
	}
    
}
