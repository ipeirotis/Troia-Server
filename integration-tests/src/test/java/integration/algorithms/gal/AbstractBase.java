package test.java.integration.algorithms.gal;

import com.datascience.core.algorithms.INewDataObserver;
import com.datascience.core.base.Algorithm;
import com.datascience.core.base.Project;
import com.datascience.core.nominal.NominalProject;
import com.datascience.datastoring.datamodels.full.MemoryJobStorage;
import com.datascience.gal.BatchDawidSkene;
import com.datascience.gal.IncrementalDawidSkene;
import org.junit.Test;

import java.util.List;

/**
 * @Author: konrad
 */
public abstract class AbstractBase {

	public NominalProject getProject(Algorithm algorithm, List<String> categories){
		MemoryJobStorage js = new MemoryJobStorage();
		return new NominalProject(algorithm, js.getNominalData("testid"), js.getNominalResults("testid", categories));
	}

	public NominalProject getBDSProject(List<String> categories){
		return getProject(new BatchDawidSkene(), categories);
	}

	public NominalProject getIDSProject(List<String> categories){
		IncrementalDawidSkene algorithm = new IncrementalDawidSkene();
		algorithm.setEpsilon(0.0001);
		algorithm.setIterations(10);
		NominalProject project = getProject(algorithm, categories);
		project.getData().addNewUpdatableAlgorithm(algorithm);
		return project;
	}

	static public void ensureComputed(Project project){
		if (!(project.getAlgorithm() instanceof INewDataObserver)){
			project.getAlgorithm().compute();
		}
	}

	@Test
	public void testBDS(){
		runTestScenario(getBDSProject(getCategories()));
	}

	@Test
	public void testIDS(){
		runTestScenario(getIDSProject(getCategories()));
	}

	abstract protected List<String> getCategories();

	abstract protected void runTestScenario(NominalProject project);
}
