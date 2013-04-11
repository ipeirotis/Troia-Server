package com.datascience.core.nominal;

import com.datascience.core.base.*;
import com.datascience.core.datastoring.memory.InMemoryNominalData;
import com.datascience.core.datastoring.memory.InMemoryResults;
import com.datascience.core.results.IResults;
import com.datascience.core.results.ResultsFactory;
import com.datascience.core.results.WorkerResult;
import com.datascience.core.results.DatumResult;
import com.datascience.utils.CostMatrix;

import java.util.Collection;

/**
 * User: artur
 */
public class NominalProject extends Project<String, INominalData, DatumResult, WorkerResult> {

	protected NominalAlgorithm nomAlgorithm;

	public NominalProject(Algorithm algorithm1){
		super(algorithm1);
		nomAlgorithm = (NominalAlgorithm) algorithm1; // just to skip casting over and over
		data = new InMemoryNominalData(); // TODO XXX FIXME we need to pass this as parameter
		algorithm.setData(data);
	}

	@Override
	public NominalAlgorithm getAlgorithm(){
		return nomAlgorithm;
	}

	@Override
	public String getKind(){
		return "NOMINAL";
	}

	public void initializeCategories(Collection<String> categories, Collection<CategoryValue> categoryPriors, CostMatrix<String> costMatrix){
		data.initialize(categories, categoryPriors, costMatrix);
		nomAlgorithm.initializeOnCategories(categories);
		results = createResultsInstance(categories);
		algorithm.setResults(results);
	}

	public static IResults<String, DatumResult, WorkerResult> createResultsInstance(Collection<String> categories){
		ResultsFactory.WorkerResultNominalFactory wrnf = new ResultsFactory.WorkerResultNominalFactory();
		wrnf.setCategories(categories);
		IResults<String, DatumResult, WorkerResult> res = new InMemoryResults<String, DatumResult, WorkerResult>(
				new ResultsFactory.DatumResultFactory(), wrnf);
		// ^^^ TODO FIXME XXX this have to be given
		return res;
	}
}
