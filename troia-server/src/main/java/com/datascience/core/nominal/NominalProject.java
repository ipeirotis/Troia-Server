package com.datascience.core.nominal;

import com.datascience.core.base.*;
import com.datascience.core.results.Results;
import com.datascience.core.results.ResultsFactory;
import com.datascience.core.results.WorkerResult;
import com.datascience.core.results.DatumResult;

import java.util.Collection;

/**
 * User: artur
 */
public class NominalProject extends Project<String, NominalData, DatumResult, WorkerResult> {

	protected NominalAlgorithm nomAlgorithm;

	public NominalProject(Algorithm algorithm1){
		super(algorithm1);
		nomAlgorithm = (NominalAlgorithm) algorithm1; // just to skip casting over and over
		data = new NominalData();
		algorithm.setData(data);
	}

	@Override
	public NominalAlgorithm getAlgorithm(){
		return nomAlgorithm;
	}

	public void initializeCategories(Collection<Category> categories){
		data.addCategories(categories);
		nomAlgorithm.initializeOnCategories(categories);
		results = createResultsInstance(categories);
		algorithm.setResults(results);
	}

	public static Results<String, DatumResult, WorkerResult> createResultsInstance(Collection<Category> categories){
		Results<String, DatumResult, WorkerResult> res = new Results<String, DatumResult, WorkerResult>(
				new ResultsFactory.DatumResultFactory(),
				new ResultsFactory.WorkerResultNominalFactory());
		((ResultsFactory.WorkerResultNominalFactory)res.getWorkerCreator()).setCategories(categories);
		return res;
	}
}
