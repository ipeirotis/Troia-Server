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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

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
public class IncrementalDawidSkene extends AbstractDawidSkene<WorkerResultIncremental> {

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

	//TODO: on data change (observable pattern)
	public void addAssignedLabel(AssignedLabel<String> al) {
		LObject<String> d = coreAssignedLabelUpdate(al);
		switch (dsmethod) {
			case ITERATELOCAL:
				for (int i = 0; i < 5; i++) // XXX: magic number
					updateObjectInformation(d, 0 != i);
				break;
			case UPDATEWORKERS:
			default:
				updateObjectInformation(d, false);
				break;
		}
		invalidateComputed();
	}

	private LObject<String> coreAssignedLabelUpdate(AssignedLabel<String> al) {
		String objectName = al.getLobject().getName();
		// If we already have the object, un-update it's prevous contribution to
		// the prior,
		// and remove the previous contribution to the workers confusion
		// matricies

		LObject<String> d = data.getObject(objectName);
		if (d != null) {
			unupdatePrior(objectName);
			unupdateWorkers(objectName);
		} else {
			d = data.getOrCreateObject(objectName);
		}
		// this would also add new worker to workers collection
		data.addAssign(al);

		return d;
	}

	private void updateObjectInformation(LObject<String> d, boolean unupdate) {
		String objectName = d.getName();
		if (unupdate) {
			unupdatePrior(objectName);
			unupdateWorkers(objectName);
		}
		results.getDatumResults().get(d).setCategoryProbabilites(getObjectClassProbabilities(objectName));
		incrementPrior(objectName);
		updateWorkers(objectName);
	}

// TODO: on data change (observable pattern)
//	@Override
//	public void addCorrectLabel(CorrectLabel cl) {
//		Datum d = coreCorrectLabelUpdate(cl);
//		switch (dsmethod) {
//			case ITERATELOCAL:
//				for (int i = 0; i < 5; i++)// XXX: magic number
//					updateObjectInformation(d, 0 != i);
//				break;
//			case UPDATEWORKERS:
//			default:
//				updateObjectInformation(d, false);
//				break;
//		}
//
//	}
//
//	private Datum coreCorrectLabelUpdate(CorrectLabel cl) {
//		String objectName = cl.getObjectName();
//		String correctCategory = cl.getCorrectCategory();
//
//		Datum d;
//		if (objects.containsKey(objectName)) {
//			unupdatePrior(objectName);
//			unupdateWorkers(objectName);
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
//
//		return d;
//	}

	private void updateWorkers(String objectName) {
		LObject<String> obj = data.getObject(objectName);
		for (AssignedLabel<String> al : data.getAssignsForObject(obj)) {
			updateWorker(objectName, al.getWorker().getName(), al.getLabel());
		}

	}

	private void updateWorker(String objectName, String workerName, String destination) {
		Map<String, Double> probs = getObjectClassProbabilities(objectName);
		Worker<String> worker = data.getWorker(workerName);
		for (String source : probs.keySet())
			results.getWokerResults().get(worker).addError(source, destination, probs.get(source));
	}

	private void unupdateWorkers(String objectName) {
		LObject<String> obj = data.getObject(objectName);
		for (AssignedLabel<String> al : data.getAssignsForObject(obj))
			unupdateWorker(objectName, al.getWorker().getName(), al.getLabel());
	}

	private void unupdateWorker(String objectName, String workerName, String destination) {
		Map<String, Double> probs = getObjectClassProbabilities(objectName);
		Worker<String> worker = data.getWorker(workerName);
		for (String source : probs.keySet())
			results.getWokerResults().get(worker).removeError(source, destination, probs.get(source));
	}

	/**
	 * removes information to the priors
	 *
	 * @param objectName
	 */
	private void unupdatePrior(String objectName) {
		priorDenominator--;
		Map<String, Double> probs = getObjectClassProbabilities(objectName);
		for (Category c: data.getCategories()) {
			double prior = probs.get(c.getName());
			double oldValue = c.getPrior();
			c.setPrior(Math.max(0, oldValue - prior));
		}
	}

	/**
	 * increases the prior using the object's probs
	 *
	 * @param objectName
	 */
	private void incrementPrior(String objectName) {
		priorDenominator++;
		Map<String, Double> probs = getObjectClassProbabilities(objectName);
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
	public Map<String, Double> getObjectClassProbabilities(String objectName, String workerToIgnore) {
		Map<String, Double> cp = results.getDatumResults().get(data.getObject(objectName)).getCategoryProbabilites();
		if (null != cp)
			return cp;
		else
			return super.getObjectClassProbabilities(objectName, workerToIgnore);
	}

	@Override
	protected void estimateInner() {
		for (LObject<String> obj :data.getObjects())
			updateObjectInformation(obj, true);
	}

	private static final Logger logger = Logger.getLogger(IncrementalDawidSkene.class);
}
