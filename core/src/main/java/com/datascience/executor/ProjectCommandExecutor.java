package com.datascience.executor;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.*;

import org.apache.log4j.Logger;

/**
 *
 * @author konrad
 */
public class ProjectCommandExecutor{
	
	private static final Logger log =
		Logger.getLogger(ProjectCommandExecutor.class);

	protected Queue<IExecutorCommand> queue;
	protected ListeningExecutorService commandExecutor;
	protected volatile boolean isAlive;
	
	public ProjectCommandExecutor(){
		this(10);  // TODO: work on this number
	}
	
	public ProjectCommandExecutor(int numThreads){
		queue = new LinkedList<IExecutorCommand>();
		ThreadFactory cetf = new ThreadFactoryBuilder()
			.setNameFormat("cmdExTh-%d")
			.build();
		commandExecutor = MoreExecutors.listeningDecorator(
			Executors.newFixedThreadPool(numThreads, cetf));
		isAlive = true;
	}

	/**
	 * Must be synchronized
	 */
	protected void checkState(){
		if (!isAlive) {
			throw new IllegalStateException("Adding command after ProjectCommandExecutor was stopped");
		}
	}

	/**
	 * Must be synchronized
	 */
	protected boolean canStop(){
		return (!isAlive) && queue.isEmpty();
	}

	public void add(final IExecutorCommand eCommand){
		synchronized (ProjectCommandExecutor.this) {
			checkState();
			if (eCommand.canStart()){
				runCommand(eCommand);
			} else {
				queue.add(eCommand);
			}
		}
	}
	
	private void runCommand(IExecutorCommand eCommand){
		ListenableFuture future = commandExecutor.submit(eCommand);
		Futures.addCallback(future, new CommandCleaner(eCommand),
			MoreExecutors.sameThreadExecutor());
	}
	
	private void executePossibleCommands(){
		synchronized (ProjectCommandExecutor.this) {
			Iterator<IExecutorCommand> iter = queue.iterator();
			while (iter.hasNext()) {
				IExecutorCommand eCommand = iter.next();
				if (eCommand.canStart()) {
					iter.remove();
					runCommand(eCommand);
				}
			}
			if (canStop()) {
				ProjectCommandExecutor.this.notify();
			}
		}
	}

	protected void initEmptyAndWaitTillEmpty(){
		synchronized (ProjectCommandExecutor.this) {
			isAlive = false;
			if (!canStop()) {
				try {
					ProjectCommandExecutor.this.wait();
				} catch (InterruptedException e) {
					log.error("Error when waiting for signal to continue ProjectCommandExecutor cleanup", e);
				}
			}
			assert canStop();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		stop();
	}
	
	public void stop() throws InterruptedException{
		log.info("STARTED Shutting down executors");
		initEmptyAndWaitTillEmpty();
		commandExecutor.shutdown();
		boolean terminated = commandExecutor.awaitTermination(1, TimeUnit.MINUTES);
		if (terminated) {
			log.info("Command inner executor closed");
		} else {
			log.error("FAILED to shutdown commandExecutor");
		}
		log.info("DONE Shutting down executors");
	}
	
	class CommandCleaner implements FutureCallback {

		private IExecutorCommand eCommand;

		public CommandCleaner(IExecutorCommand eCommand){
			this.eCommand = eCommand;
		}

		@Override
		public void onSuccess(Object v) {
			cleanUp();
		}

		@Override
		public void onFailure(Throwable thrwbl) {
			String className = eCommand.getClass().getName();
			log.error("Failed executing task: " + className, thrwbl);
			cleanUp();
		}

		private void cleanUp() {
			eCommand.cleanup();
			executePossibleCommands();
		}
	}
}
