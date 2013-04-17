package com.datascience.core.nominal;

import com.datascience.core.base.*;
import com.datascience.core.results.IResults;
import com.datascience.core.results.WorkerResult;
import com.datascience.core.results.DatumResult;
import com.datascience.utils.CostMatrix;

import java.util.Collection;

/**
 * User: artur
 */
public class NominalProject extends Project<String, INominalData, DatumResult, WorkerResult> {

	protected NominalAlgorithm nomAlgorithm;

	public NominalProject(Algorithm algorithm1, INominalData data, IResults<String, DatumResult, WorkerResult> results){
		super(algorithm1, data, results);
		nomAlgorithm = (NominalAlgorithm) algorithm1; // just to skip casting over and over
		algorithm.setData(data);
		algorithm.setResults(results);
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
		nomAlgorithm.initializeOnCategories();
	}
}
