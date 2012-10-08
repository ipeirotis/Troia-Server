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

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;

import com.datascience.gal.AssignedLabel;
import com.datascience.gal.Category;
import com.datascience.gal.CorrectLabel;
import com.datascience.gal.DawidSkene;
import com.datascience.gal.MisclassificationCost;
import com.datascience.gal.service.DawidSkeneCache;

/**
 * This class manages Dawid-Skene processors and executes them in thread poll.
 */
public class DawidSkeneProcessorManager extends Thread {

	/**
	 * @param threadPoolSize
	 *            How many threads will be dedicated to DS processors execution
	 * @param sleepPeriod
	 *            How much time, in milliseconds, will pass between manager will
	 *            check if there is porcessor ready for execution
	 */
	public DawidSkeneProcessorManager(int threadPoolSize, int sleepPeriod,
			String user, String password, String db, String url, int cachesize)
			throws ClassNotFoundException, SQLException, IOException {
		this.processorQueue = new HashMap<String, Queue<DawidSkeneProcessor>>();
		this.executor = Executors.newFixedThreadPool(threadPoolSize);
		this.stopped = false;
		this.sleepPeriod = sleepPeriod;
		this.cache = new DawidSkeneCache(user, password, db, url, cachesize);
	}

	public DawidSkeneProcessorManager(int threadPoolSize, int sleepPeriod,
			String user, String password, String db, String url)
			throws ClassNotFoundException, SQLException, IOException {
		this.processorQueue = new HashMap<String, Queue<DawidSkeneProcessor>>();
		this.executor = Executors.newFixedThreadPool(threadPoolSize);
		this.stopped = false;
		this.sleepPeriod = sleepPeriod;
		this.cache = new DawidSkeneCache(user, password, db, url);
	}

	@Override
	public void run() {
		logger.info("Starting DawidSkene processor manager.");
		while (!this.stopped) {
			this.executeProcessors();
			try {
				sleep(this.sleepPeriod);
			} catch (InterruptedException e) {
				logger.debug("DS processor manager sleep interrupted by new processor addition.");
			}
		}
		logger.info("DawidSkene processor manager stopped.");
	}

	public void createProject(String projectId,
			Collection<Category> categories, boolean incremental) {
		synchronized (this.processorQueue) {
			this.cache.createDawidSkene(projectId, this, categories,
					incremental);
			this.processorQueue.put(projectId,
					new ConcurrentLinkedQueue<DawidSkeneProcessor>());
		}
	}

	public void deleteProject(String projectId) {
		if (this.containsProject(projectId)) {
			this.processorQueue.remove(projectId);
			this.cache.removeFromCache(projectId);
			this.executor.execute(new DawidSkeneRemover(projectId, this.cache));
		}
	}

	public boolean containsProject(String projectId) {
		if (this.processorQueue.containsKey(projectId)) {
			return true;
		} else {
			return this.cache.hasDawidSkene(projectId);
		}
	}

	public void addLabels(String projectId, Collection<AssignedLabel> labels) {
		LabelWriter writer = new LabelWriter(projectId, this.cache, labels);
		this.addProcessor(writer);
	}

	public void addGoldLabels(String projectId,
			Collection<CorrectLabel> goldLabels) {
		GoldLabelWriter writer = new GoldLabelWriter(projectId, this.cache,
				goldLabels);
		this.addProcessor(writer);
	}
	
	public void addMisclassificationCost(String projectId,Collection<MisclassificationCost> costs){
		MisclassificationCostsWriter writer = new MisclassificationCostsWriter(projectId,this.cache,costs);
		this.addProcessor(writer);
	}

	public void computeDawidSkene(String projectId, int iterations) {
		DSalgorithmComputer computer = new DSalgorithmComputer(projectId,
				this.cache, iterations);
		this.addProcessor(computer);
	}

	public void updateDawidSkene(String projectId, DawidSkene updatedDS) {
		CacheUpdater updater = new CacheUpdater(projectId, this.cache,
				updatedDS);
		this.addProcessor(updater);
	}

	public final DawidSkene getDawidSkeneForReadOnly(String projectId) {
		Queue<DawidSkeneProcessor> processors;
		DawidSkene ds = null;
		synchronized (this.processorQueue) {
			processors = this.processorQueue.get(projectId);
			if (processors == null
					|| processors.size() == 0
					|| !processors.peek().getState()
							.equals(DawidSkeneProcessorState.RUNNING)) {
				ds = this.cache.getDawidSkeneForReadOnly(projectId,
						Thread.currentThread());
			}
		}
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				logger.debug("Reader thread interrupted.");
			} finally {
				synchronized (this.processorQueue) {
					if (processors.size() == 0
							|| !processors.peek().getState()
									.equals(DawidSkeneProcessorState.RUNNING)) {
						ds = this.cache.getDawidSkeneForReadOnly(projectId,
								Thread.currentThread());
						break;
					}
				}
			}
		}

		return ds;
	}
	
	public void finalizeReading(String projectId){
		this.cache.finalizeReading(projectId,Thread.currentThread());
	}

	/**
	 * This function adds a new processor to manager
	 * 
	 * @param processor
	 *            Processor that's going to be added to manager
	 */
	private void addProcessor(DawidSkeneProcessor processor) {
		synchronized (this.processorQueue) {
			if (processor.getState().equals(DawidSkeneProcessorState.CREATED)) {
				if (!this.processorQueue.containsKey(processor
						.getDawidSkeneId())) {
					this.processorQueue.put(processor.getDawidSkeneId(),
							new ConcurrentLinkedQueue<DawidSkeneProcessor>());
				}
				this.processorQueue.get(processor.getDawidSkeneId()).add(
						processor);
				processor.setState(DawidSkeneProcessorState.IN_QUEUE);
				logger.info("Added new processor to queue");
				this.interrupt();
			}
		}
	}

	public int getProcessorCountForProject(String projectId) {
		int processorCount = 0;
		synchronized (this.processorQueue) {
			Queue<DawidSkeneProcessor> queue = this.processorQueue
					.get(projectId);
			if (queue != null) {
				processorCount = queue.size();
			}
		}
		return processorCount;
	}

	private void executeProcessors() {
		synchronized (this.processorQueue) {
			Collection<String> projects = this.processorQueue.keySet();
			for (String project : projects) {
				Queue<DawidSkeneProcessor> queue = this.processorQueue
						.get(project);
				if (queue.peek() != null
						&& !queue.peek().getState()
								.equals(DawidSkeneProcessorState.RUNNING)) {
					if (queue.peek().getState()
							.equals(DawidSkeneProcessorState.FINISHED)) {
						queue.poll();
					}
					if (queue.peek() != null) {
						this.executor.execute(queue.peek());
						queue.peek().setState(DawidSkeneProcessorState.RUNNING);
					}
				}
			}
		}
	}

	/**
	 * Map that holds queues of processors for each project
	 */
	private Map<String, Queue<DawidSkeneProcessor>> processorQueue;

	/**
	 * @return Map that holds queues of processors for each project
	 */
	public Map<String, Queue<DawidSkeneProcessor>> getProcessorQueue() {
		return processorQueue;
	}

	/**
	 * @param processorQueue
	 *            Map that holds queues of processors for each project
	 */
	public void setProcessorQueue(
			Map<String, Queue<DawidSkeneProcessor>> processorQueue) {
		this.processorQueue = processorQueue;
	}

	/**
	 * Executor that executes processor threads
	 */
	private ExecutorService executor;

	/**
	 * @return Executor that executes processor threads
	 */
	public ExecutorService getExecutor() {
		return executor;
	}

	/**
	 * @param executor
	 *            Executor that executes processor threads
	 */
	public void setExecutor(ExecutorService executor) {
		this.executor = executor;
	}

	/**
	 * Set to true if manager is stopped
	 */
	private boolean stopped;

	/**
	 * @return Set to true if manager is stopped
	 */
	public boolean isStopped() {
		return stopped;
	}

	/**
	 * @param stopped
	 *            Set to true if manager is stopped
	 */
	public void setStopped() {
		this.stopped = true;
	}

	/**
	 * Time for wtih manager thread will be put to sleep between executions
	 */
	private int sleepPeriod;

	/**
	 * @return Time for wtih manager thread will be put to sleep between
	 *         executions
	 */
	public int getSleepPeriod() {
		return sleepPeriod;
	}

	/**
	 * @param sleepPeriod
	 *            Time for wtih manager thread will be put to sleep between
	 *            executions
	 */
	public void setSleepPeriod(int sleepPeriod) {
		this.sleepPeriod = sleepPeriod;
	}

	/**
	 * Cache containing objects that processors work on
	 */
	private DawidSkeneCache cache;

	private List<Thread> readerThreads;

	private static Logger logger = Logger
			.getLogger(DawidSkeneProcessorManager.class);
}
