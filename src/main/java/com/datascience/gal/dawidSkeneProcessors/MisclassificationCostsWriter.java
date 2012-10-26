/*******************************************************************************
 * Copyright (c) 2012 Panagiotis G. Ipeirotis & Josh M. Attenberg
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/

package com.datascience.gal.dawidSkeneProcessors;

import java.util.Collection;

import org.apache.log4j.Logger;

import com.datascience.gal.DawidSkene;
import com.datascience.gal.MisclassificationCost;
import com.datascience.gal.service.DawidSkeneCache;

public class MisclassificationCostsWriter extends DawidSkeneProcessor {

	/**
	 * @param id Dawid-Skene project identifier
	 * @param cache Dawid-Skene cache that will be used by this writer
	 * @param costs Collection of misclassification costs that will be added to model
	 */
	public MisclassificationCostsWriter(String id,DawidSkeneCache cache,Collection<MisclassificationCost> costs) {
		super(id,cache);
		this.setCosts(costs);
		logger.info("Created misclassification cost writer for " + id + ".");
	}

	@Override
	public void run() {
		DawidSkene ds = this.getCache().getDawidSkeneForEditing(this.getDawidSkeneId(),this);
		ds.addMisclassificationCosts(costs);
		this.getCache().insertDawidSkene(ds,this);
	}

	/**
	 * Collection of misclassification costs that will be added
	 */
	private Collection<MisclassificationCost> costs;

	/**
	 * @return Collection of misclassification costs that will be added
	 */
	public Collection<MisclassificationCost> getCosts() {
		return costs;
	}

	/**
	 * @param costs Collection of misclassification costs that will be added
	 */
	public void setCosts(Collection<MisclassificationCost> costs) {
		this.costs = costs;
	}

	private static Logger logger = Logger.getLogger(MisclassificationCostsWriter.class);
}
