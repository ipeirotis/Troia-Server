/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.datascience.gal;

import java.lang.reflect.Type;
import java.util.*;

import com.datascience.core.algorithms.INewDataObserver;
import com.datascience.core.base.*;
import com.datascience.core.nominal.IncrementalNominalModel;
import com.datascience.core.results.DatumResult;
import com.datascience.core.results.WorkerResult;
import com.datascience.core.nominal.CategoryPriorCalculators;
import com.datascience.core.stats.ErrorRateCalculators;
import com.google.gson.reflect.TypeToken;

import static com.datascience.core.nominal.ProbabilityDistributions.getSpammerDistribution;

/**
 * fully incremental version of DS- adjustments to the data structures are made
 * when new information becomes available, not waiting for an explicit
 * estimation step
 *
 * @author josh
 *
 */
public class IncrementalDawidSkene extends AbstractDawidSkene
			implements INewDataObserver<String> {

	private IncrementalNominalModel model;

	public IncrementalDawidSkene() {
		super(
			new ErrorRateCalculators.IncrementalErrorRateCalculator(),
			new CategoryPriorCalculators.IncrementalCategoryPriorCalculator());
		model = new IncrementalNominalModel();
	}

	@Override
	public IncrementalNominalModel getModel() {
		return model;
	}

	@Override
	public Type getModelType() {
		return new TypeToken<IncrementalNominalModel>() {} .getType();
	}

	@Override
	public void setModel(Object o){
		model = (IncrementalNominalModel) o;
	}

	@Override
	public void compute() {
	}

	@Override
	public void newAssign(AssignedLabel<String> assign) {
		DatumResult dr = results.getOrCreateDatumResult(assign.getLobject());
		Map<String, Double> oldProbabilites = dr.getCategoryProbabilites();
		//update object class probabilites
		Map<String, Double> probabilitiesWithWorkerInfluence = getObjectClassProbabilities(assign.getLobject(), null);
		dr.setCategoryProbabilites(probabilitiesWithWorkerInfluence);
		results.addDatumResult(assign.getLobject(), dr);
		//update priors
		if (!data.arePriorsFixed()){
			undoPriorInfluence(oldProbabilites);
			makePriorInfluence(probabilitiesWithWorkerInfluence);
		}
		//rebuild worker confusion matrices for all workers who assigned this object
		//probabilities would be null only if we've got an object with just one assign. is such a situation we return prior distribution
		//(https://project-troia.atlassian.net/browse/TROIA-347?focusedCommentId=12704&page=com.atlassian.jira.plugin.system.issuetabpanels:comment-tabpanel#comment-12704)
		Map<String, Double> probabilities = getObjectClassProbabilities(assign.getLobject(), assign.getWorker());
		if (probabilities == null)
			probabilities = getSpammerDistribution(data, this);
		for (AssignedLabel<String> al : data.getAssignsForObject(assign.getLobject())){
			WorkerResult wr = results.getOrCreateWorkerResult(al.getWorker());
			for (Map.Entry<String, Double> e : probabilities.entrySet()){
				wr.addError(e.getKey(), al.getLabel(), e.getValue());
			}
			results.addWorkerResult(al.getWorker(), wr);
		}

	}

	private void undoPriorInfluence(Map<String, Double> probabilites){
		if (probabilites != null && probabilites.size() > 0){
			model.priorDenominator--;
			for (Map.Entry<String, Double> e : model.categoryPriors.entrySet()){
				e.setValue(e.getValue() - probabilites.get(e.getKey()));
			}
		}
	}

	private void makePriorInfluence(Map<String, Double> probabilites){
		if (probabilites != null){
			model.priorDenominator++;
			for (Map.Entry<String, Double> e : model.categoryPriors.entrySet()){
				e.setValue(e.getValue() + probabilites.get(e.getKey()));
			}
		}
	}

	@Override
	public void newGoldObject(LObject<String> object) {
	}

	@Override
	public void newObject(LObject<String> object) {
	}

	@Override
	public void newWorker(Worker<String> worker) {
	}
}

