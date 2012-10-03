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
import com.datascience.gal.BatchDawidSkene;
import com.datascience.gal.service.DawidSkeneCache;



/**
 * This is processor that updates cache with given Dawid-Skene model
 * Use of this class is highly depreciated as it was written only to
 * provide backwards compatibility of Troia by supporting "computeBlocking"
 * service. If you are writing code that modifies Dawid-Skene model and writes
 * it to cache you should create class that extends DawidSkeneProcessor and
 * put yours code there. If you do so you can simply call this.getCahce().insertDawidSkene(myDs)
 * in it.
 */
public class CacheUpdater extends DawidSkeneProcessor {



	/**
	 * @param id Dawid-Skene project identifier
	 * @param cache Dawid-Skene cache that will be used by this writer
	 * @param ds Dawid-Skene model that will be written to cache
	 */
	public CacheUpdater(String id,DawidSkeneCache cache,DawidSkene ds) {
		super(id,cache);
		this.setDs(ds);
	}


	@Override
	public void run() {
		logger.info("Executing cahce updater (depreciated) for "+this.getDawidSkeneId()+".");
		this.getCache().getDawidSkeneForEditing(this.getDawidSkeneId(),this);
		this.getCache().insertDawidSkene(ds,this);
		this.setState(DawidSkeneProcessorState.FINISHED);
	}


	/**
	 * DawidSkene model that will be written to cache
	 */
	private DawidSkene ds;

	/**
	 * @return DawidSkene model that will be written to cache
	 */
	public DawidSkene getDs() {
		return ds;
	}

	/**
	 * @param ds DawidSkene model that will be written to cache
	 */
	public void setDs(DawidSkene ds) {
		this.ds = ds;
	}

	private static Logger logger = Logger.getLogger(CacheUpdater.class);

}
