package com.datascience.gal;

import com.datascience.core.algorithms.INewDataObserver;
import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import com.datascience.core.nominal.NominalAlgorithm;
import com.datascience.core.nominal.NominalProject;
import com.datascience.core.nominal.decision.DecisionEngine;
import com.datascience.core.nominal.decision.LabelProbabilityDistributionCostCalculators;
import com.datascience.core.nominal.decision.WorkerEstimator;
import com.datascience.datastoring.datamodels.full.MemoryJobStorage;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertFalse;


/**
 * User: artur
 * Date: 5/28/13
 */
@RunWith(Parameterized.class)
public class EdgeCasesTest {

	private static Logger logger = Logger.getLogger(EdgeCasesTest.class);
	private static List<String> categories = Arrays.asList(new String[]{"category1", "category2", "category3"});

	public static abstract class ProjectCreator{
		abstract NominalProject create();
		protected NominalProject getProject(NominalAlgorithm algorithm){
			MemoryJobStorage js = new MemoryJobStorage();
			return new NominalProject(
					algorithm,
					js.getNominalData("testid"),
					js.getNominalResults("testid", categories));
		}
	}

	public interface AssignsCreator{
		AssignedLabel<String>[] create();
	}

	final static ProjectCreator[] PROJECT_CREATORS = new ProjectCreator[]{
		new ProjectCreator() {
			@Override
			public NominalProject create() {
				return getProject(new BatchDawidSkene());
			}
			@Override
			public String toString(){
				return "BDS";
			}
		},
		new ProjectCreator() {
			@Override
			NominalProject create() {
				IncrementalDawidSkene algorithm = new IncrementalDawidSkene();
				algorithm.setEpsilon(0.0001);
				algorithm.setIterations(10);
				NominalProject project = getProject(algorithm);
				project.getData().addNewUpdatableAlgorithm(algorithm);
				return project;
			}
			@Override
			public String toString(){
				return "IDS";
			}
		},
//		new ProjectCreator() {
//			@Override
//			public NominalProject create() {
//				return getProject(new BatchMV());
//			}
//			@Override
//			public String toString(){
//				return "BMV";
//			}
//		},
//		new ProjectCreator() {
//			@Override
//			public NominalProject create() {
//				IncrementalMV algorithm = new IncrementalMV();
//				NominalProject project = getProject(algorithm);
//				project.getData().addNewUpdatableAlgorithm(algorithm);
//				return project;
//			}
//			@Override
//			public String toString(){
//				return "IMV";
//			}
//		},
	};

	final static AssignsCreator[] ASSIGNS_CREATORS = new AssignsCreator[]{
		new AssignsCreator() {
			@Override
			public AssignedLabel<String>[] create() {
				return new AssignedLabel[]{};
			}
			@Override
			public String toString(){
				return "NO ASSIGNS";
			}
		},
		new AssignsCreator() {
			@Override
			public AssignedLabel<String>[] create() {
				return new AssignedLabel[]{new AssignedLabel<String>(new Worker("worker"), new LObject<String>("object"), categories.get(0))};
			}
			@Override
			public String toString(){
				return "JUST ONE ASSIGN";
			}
		},
		new AssignsCreator() {
			@Override
			public AssignedLabel<String>[] create() {
				return new AssignedLabel[]{
						new AssignedLabel<String>(new Worker("worker1"), new LObject<String>("object"), categories.get(0)),
						new AssignedLabel<String>(new Worker("worker2"), new LObject<String>("object"), categories.get(1)),
						new AssignedLabel<String>(new Worker("worker3"), new LObject<String>("object"), categories.get(0)),
						new AssignedLabel<String>(new Worker("worker4"), new LObject<String>("object"), categories.get(2))};
			}
			@Override
			public String toString(){
				return "ONE OBJECT WITH MANY ASSIGNS";
			}
		},
		new AssignsCreator() {
			@Override
			public AssignedLabel<String>[] create() {
				return new AssignedLabel[]{
						new AssignedLabel<String>(new Worker("worker"), new LObject<String>("object1"), categories.get(0)),
						new AssignedLabel<String>(new Worker("worker"), new LObject<String>("object2"), categories.get(1)),
						new AssignedLabel<String>(new Worker("worker"), new LObject<String>("object3"), categories.get(0)),
						new AssignedLabel<String>(new Worker("worker"), new LObject<String>("object4"), categories.get(2))};
			}
			@Override
			public String toString(){
				return "MANY OBJECTS EACH WITH ONE ASSIGN, ONE WORKER";
			}
		},
		new AssignsCreator() {
			@Override
			public AssignedLabel<String>[] create() {
				return new AssignedLabel[]{
						new AssignedLabel<String>(new Worker("worker1"), new LObject<String>("object1"), categories.get(0)),
						new AssignedLabel<String>(new Worker("worker2"), new LObject<String>("object2"), categories.get(1)),
						new AssignedLabel<String>(new Worker("worker3"), new LObject<String>("object3"), categories.get(0)),
						new AssignedLabel<String>(new Worker("worker4"), new LObject<String>("object4"), categories.get(2))};
			}
			@Override
			public String toString(){
				return "MANY OBJECTS EACH WITH ONE ASSIGN, MANY WORKERS";
			}
		}
	};

	public ProjectCreator projectCreator;
	public AssignsCreator assignsCreator;
	public NominalProject project;

	public EdgeCasesTest(ProjectCreator creator, AssignsCreator assignsCreator){
		this.projectCreator = creator;
		this.assignsCreator = assignsCreator;
	}

	@Parameterized.Parameters(name= "alg: {0}, assigns: {1}")
	public static Collection<Object[]> instancesToTest() {
		Collection<Object[]> ret = new LinkedList<Object[]>();
		for (ProjectCreator dc : PROJECT_CREATORS)
			for (AssignsCreator ac : ASSIGNS_CREATORS)
				ret.add(new Object[]{dc, ac});
		return ret;
	}

	@Before
	public void initialize(){
		project = projectCreator.create();
		project.initializeCategories(categories, null, null);
		for (AssignedLabel<String> al : assignsCreator.create())
			project.getData().addAssign(al);
		if (!(project.getAlgorithm() instanceof INewDataObserver)){
			project.getAlgorithm().compute();
		}
	}

	@Test
	public void testWorkerCost(){
		WorkerEstimator we = new WorkerEstimator(LabelProbabilityDistributionCostCalculators.get("ExpectedCost"));
		for (Double d : we.getCosts(project).values()) {
			assertFalse(Double.isNaN(d));
		}
	}

	@Test
	public void testDataCost(){
		DecisionEngine de = new DecisionEngine(LabelProbabilityDistributionCostCalculators.get("ExpectedCost"), null);
		for (Double d : de.estimateMissclassificationCosts(project).values()) {
			assertFalse(Double.isNaN(d));
		}
	}
}
