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

import com.datascience.gal.Category;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.IncrementalDawidSkene;
import com.datascience.gal.BatchDawidSkene;
import com.datascience.gal.service.DawidSkeneCache;

/**
 * This processor creates new Dawid-Skene model and uploads categories to it.
 */
public class CategoryWriter extends DawidSkeneProcessor {

	/**
	 * @param id Dawid-Skene project identifier
	 * @param cache Dawid-Skene cache that will be used by this writer
	 * @param categories Categories that will initialize Dawid-Skene model
	 * @param incremental Setes if this Dawid-Skene model will be incremental or batch
	 */
	public CategoryWriter(String id,DawidSkeneCache cache,Collection<Category> categories,boolean incremental) {
		super(id,cache);
		this.setCategories(categories);
		this.setIncremental(incremental);
	}

	@Override
	public void run() {
		logger.info("Executing category writer for "+this.getDawidSkeneId()+".");
		DawidSkene ds;
		if (this.incremental) {
			ds = new IncrementalDawidSkene(this.getDawidSkeneId(), this.categories);
		} else {
			ds = new BatchDawidSkene(this.getDawidSkeneId(), this.categories);
		}
		this.getCache().insertDawidSkene(ds);
		this.setState(DawidSkeneProcessorState.FINISHED);
		logger.info("Category writer for "+this.getDawidSkeneId()+" finished.");
	}


	/**
	 * Categories that going to initialise DS model
	 */
	private Collection<Category> categories;

	/**
	 * @return Categories that going to initialise DS model
	 */
	public Collection<Category> getCategories() {
		return categories;
	}

	/**
	 * @param categories Categories that going to initialise DS model
	 */
	public void setCategories(Collection<Category> categories) {
		this.categories = categories;
	}

	/**
	 * True if DS model is incremental and false if it is batch
	 */
	private boolean incremental;

	/**
	 * @return True if DS model is incremental and false if it is batch
	 */
	public boolean isIncremental() {
		return incremental;
	}

	/**
	 * @param incremental True if DS model is incremental and false if it is batch
	 */
	public void setIncremental(boolean incremental) {
		this.incremental = incremental;
	}


	private static Logger logger = Logger.getLogger(CategoryWriter.class);
}
