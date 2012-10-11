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
import com.datascience.gal.AssignedLabel;
import com.datascience.gal.service.DawidSkeneCache;

public class LabelWriter extends DawidSkeneProcessor {

	public LabelWriter(String id,DawidSkeneCache cache,Collection<AssignedLabel> labels) {
		super(id,cache);
		this.setLabels(labels);
	}

	@Override
	public void run() {
		logger.info("Executing label writer for "+this.getDawidSkeneId()+".");
		DawidSkene ds = this.getCache().getDawidSkeneForEditing(this.getDawidSkeneId(),this);
		ds.addAssignedLabels(labels);
		this.getCache().insertDawidSkene(ds,this);
		this.setState(DawidSkeneProcessorState.FINISHED);
		logger.info("Label writer for "+this.getDawidSkeneId()+" finished.");
	}

	/**
	 * Worker assigned labels that will be added to Dawid-Skene model1
	 */
	private Collection<AssignedLabel> labels;

	/**
	 * @return Worker assigned labels that will be added to Dawid-Skene model1
	 */
	public Collection<AssignedLabel> getLabels() {
		return labels;
	}

	/**
	 * @param labels Worker assigned labels that will be added to Dawid-Skene model1
	 */
	public void setLabels(Collection<AssignedLabel> labels) {
		this.labels = labels;
	}

	private static Logger logger = Logger.getLogger(LabelWriter.class);

}
