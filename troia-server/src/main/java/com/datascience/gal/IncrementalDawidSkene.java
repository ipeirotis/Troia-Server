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

import java.util.*;

import com.datascience.core.algorithms.IUpdatableAlgorithm;
import com.datascience.core.base.AssignedLabel;
import com.datascience.core.base.LObject;
import com.datascience.core.base.Worker;
import org.apache.log4j.Logger;

/**
 * fully incremental version of DS- adjustments to the data structures are made
 * when new information becomes available, not waiting for an explicit
 * estimation step
 *
 * TODO: batch updates TODO: override addAssignedLabels and addCorrectLabels
 *
 * @author josh
 *
 */
public class IncrementalDawidSkene extends AbstractDawidSkene<WorkerResultIncremental>
			implements IUpdatableAlgorithm<String>{

	private IncrementalDSMethod dsmethod = IncrementalDSMethod.UPDATEWORKERS;
	private double priorDenominator;

	public IncrementalDawidSkene(Collection<Category> categories) {
		super(categories);
		super.logger = this.logger;
	}

	@Override
	protected void initializePriors() {
		for (Category c : data.getCategories())
			c.setPrior(0);
		priorDenominator = 0;
	}

	@Override
	public double prior(String categoryName) {
		if (fixedPriors || priorDenominator == 0)
			return 1. / (double) data.getCategories().size();
		else
			return data.getCategory(categoryName).getPrior() / priorDenominator;
	}

	@Override
	public void newAssign(AssignedLabel<String> al) {
		LObject<String> d = coreAssignedLabelUpdate(al);
		computeForObject(d);
		invalidateComputed();
	}

	/**
	 * This function needs to know whether this is compleately new assign or "remake"
	 * @param al
	 * @return
	 */
	private LObject<String> coreAssignedLabelUpdate(AssignedLabel<String> al) {
		// If we already have the object, un-update it's prevous contribution to
		// the prior,
		// and remove the previous contribution to the workers confusion
		// matricies

		LObject<String> object = al.getLobject();
//		if (d != null) {
//			// this should work on old previous assign ..
//			unupdatePrior(object);
//			unupdateWorkers(object);
//		} else {
//			d = data.getOrCreateObject(objectName);
//		}
		// this would also add new worker to workers collection
//		data.addAssign(al);
		// ^^^^^^ XXX TODO FIXME

		return object;
	}

	private void updateObjectInformation(LObject<String> object, boolean unupdate) {
		if (unupdate) {
			unupdatePrior(object);
			unupdateWorkers(object);
		}
		incrementPrior(object);
		updateWorkers(object);
	}

	public void computeForObject(LObject<String> object){
		switch (dsmethod) {
			case ITERATELOCAL:
				for (int i = 0; i < 5; i++)// XXX: magic number
					updateObjectInformation(object, 0 != i);
				break;
			case UPDATEWORKERS:
			default:
				updateObjectInformation(object, false);
				break;
		}
	}
//// TODO: on data change (observable pattern)
	@Override
	public void newGoldObject(LObject<String> goldObject) {
		coreCorrectLabelUpdate(goldObject);
		computeForObject(goldObject);
	}

	/**
	 * This function needs to know whether this is compleately new assign or "remake"
	 * @return
	 */
	private void coreCorrectLabelUpdate(LObject<String> goldObject) {
//		String objectName = cl.getObjectName();
//		String correctCategory = cl.getCorrectCategory();
//
//		if (objects.containsKey(objectName)) {
//			unupdatePrior(goldObject);
//			unupdateWorkers(goldObject);
//			d = objects.get(objectName);
//		} else {
//			Set<Category> categories = new HashSet<Category>(
//				this.categories.values());
//			d = new Datum(objectName, categories);
//		}
//		if (objectsWithNoLabels.containsKey(objectName)) {
//			objectsWithNoLabels.remove(objectName);
//		}
//		d.setGold(true);
//		d.setCorrectCategory(correctCategory);
//		objects.put(objectName, d);

	}

	private void updateWorkers(LObject<String> obj) {
		for (AssignedLabel<String> al : data.getAssignsForObject(obj)) {
			updateWorker(al);
		}
	}

	private void updateWorker(AssignedLabel<String> assign) {
		Map<String, Double> probs = getObjectClassProbabilities(assign.getLobject());
		Worker<String> worker = assign.getWorker();
		for (String source : probs.keySet())
			results.getWokerResults().get(worker).addError(source, assign.getLabel(), probs.get(source));
	}

	private void unupdateWorkers(LObject<String> obj) {
		for (AssignedLabel<String> al : data.getAssignsForObject(obj))
			unupdateWorker(al);
	}

	private void unupdateWorker(AssignedLabel<String> assign) {
		Map<String, Double> probs = getObjectClassProbabilities(assign.getLobject());
		Worker<String> worker = assign.getWorker();
		for (String source : probs.keySet())
			results.getWokerResults().get(worker).removeError(source, assign.getLabel(), probs.get(source));
	}

	/**
	 * removes information to the priors
	 */
	private void unupdatePrior(LObject<String> object) {
		priorDenominator--;
		Map<String, Double> probs = getObjectClassProbabilities(object);
		for (Category c: data.getCategories()) {
			double prior = probs.get(c.getName());
			double oldValue = c.getPrior();
			c.setPrior(Math.max(0, oldValue - prior));
		}
	}

	/**
	 * increases the prior using the object's probs
	 */
	private void incrementPrior(LObject<String> object) {
		priorDenominator++;
		Map<String, Double> probs = getObjectClassProbabilities(object);
		for (Category c: data.getCategories()) {
			double prior = probs.get(c.getName());
			double oldValue = c.getPrior();
			c.setPrior(oldValue + prior);
		}
	}

	public Map<String, Double> getCategoryPriors() {
		Map<String, Double> out = new HashMap<String, Double>(data.getCategories().size());
		for (Category c : data.getCategories())
			out.put(c.getName(), prior(c.getName()));
		return out;
	}

	@Override
	public Map<String, Double> getObjectClassProbabilities(LObject<String> object, Worker<String> workerToIgnore) {
		Map<String, Double> cp = results.getDatumResults().get(object).getCategoryProbabilites();
		if (null != cp)
			return cp;
		else
			return super.getObjectClassProbabilities(object, workerToIgnore);
	}

	@Override
	protected void estimateInner() {
		for (LObject<String> obj :data.getObjects())
			updateObjectInformation(obj, true);
	}

	private static final Logger logger = Logger.getLogger(IncrementalDawidSkene.class);

	@Override
	public void newObject(LObject<String> object) {
		// This algorithm is not interested in this ??
	}

	@Override
	public void newWorker(Worker<String> worker) {
		// This algorithm is not interested in this ??
	}
}
