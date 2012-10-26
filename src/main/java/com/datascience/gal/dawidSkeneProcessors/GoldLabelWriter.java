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

import com.datascience.gal.CorrectLabel;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.service.DawidSkeneCache;


public class GoldLabelWriter extends DawidSkeneProcessor {

	/**
	 * @param id Dawid-Skene project identifier
	 * @param cache Dawid-Skene cache that will be used by this writer
	 */
	public GoldLabelWriter(String id,DawidSkeneCache cache,Collection<CorrectLabel> goldLabels) {
		super(id,cache);
		this.setGoldLabels(goldLabels);
	}

	@Override
	public void run() {
		logger.info("Executing gold label writer for "+this.getDawidSkeneId()+".");
		DawidSkene ds = this.getCache().getDawidSkeneForEditing(this.getDawidSkeneId(),this);
		ds.addCorrectLabels(this.goldLabels);
		this.getCache().insertDawidSkene(ds,this);
		this.setState(DawidSkeneProcessorState.FINISHED);
		logger.info("Gold label writer for "+this.getDawidSkeneId()+" finished.");
	}


	/**
	 * Gold labels that will be added to Dawid-Skene model
	 */
	private Collection<CorrectLabel> goldLabels;

	/**
	 * @return Gold labels that will be added to Dawid-Skene model
	 */
	public Collection<CorrectLabel> getGoldLabels() {
		return goldLabels;
	}

	/**
	 * @param goldLabels Gold labels that will be added to Dawid-Skene model
	 */
	public void setGoldLabels(Collection<CorrectLabel> goldLabels) {
		this.goldLabels = goldLabels;
	}

	private static Logger logger = Logger.getLogger(GoldLabelWriter.class);

}
