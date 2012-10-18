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

import org.apache.log4j.Logger;

import com.datascience.gal.DawidSkene;
import com.datascience.gal.service.DawidSkeneCache;

/**
 * Objects of this class execute Dawid-Skene algorithm on DS model with given
 * id.
 */
public class DSalgorithmComputer extends DawidSkeneProcessor {

	public DSalgorithmComputer(String id, DawidSkeneCache cache, int iterations) {
		super(id, cache);
		this.setIterations(iterations);
	}

	@Override
	public void run() {
		logger.info("Started DS-computer for " + this.getDawidSkeneId()
				+ " with " + this.iterations + " iterations.");
		DawidSkene ds = this.getCache().getDawidSkeneForEditing(
				this.getDawidSkeneId(), this);
		if (!ds.isComputed()) {
			ds.estimate(this.iterations);
		}
		this.getCache().insertDawidSkene(ds, this);
		this.setState(DawidSkeneProcessorState.FINISHED);
		logger.info("DS-computer for " + this.getDawidSkeneId() + " finished.");
	}

	/**
	 * How many iterations of Dawid-Skene algorithm will be run
	 */
	private int iterations;

	/**
	 * @return How many iterations of Dawid-Skene algorithm will be run
	 */
	public int getIterations() {
		return iterations;
	}

	/**
	 * @param iterations
	 *            How many iterations of Dawid-Skene algorithm will be run
	 */
	public void setIterations(int iterations) {
		this.iterations = iterations;
	}

	private static Logger logger = Logger.getLogger(LabelWriter.class);
}
