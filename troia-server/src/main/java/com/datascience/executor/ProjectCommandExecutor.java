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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 *
 * @author konrad
 */
public class ProjectCommandExecutor{
	
	private static final Logger log =
		Logger.getLogger(ProjectCommandExecutor.class.getName());

	private Queue<IExecutorCommand> queue;
	private ListeningExecutorService commandExecutor;
	private ExecutorService lockExecutor;
	
	public ProjectCommandExecutor(){
		this(10);  // TODO: work on this number
	}
	
	public ProjectCommandExecutor(int numThreads){
		queue = new LinkedList<IExecutorCommand>();
		ThreadFactory cetf = new ThreadFactoryBuilder()
			.setNameFormat("cmdExTh-%d")
			.build();
		ThreadFactory ltf = new ThreadFactoryBuilder()
			.setNameFormat("lockExTh-%d")
			.build();
		commandExecutor = MoreExecutors.listeningDecorator(
			Executors.newFixedThreadPool(numThreads, cetf));
		lockExecutor = Executors.newSingleThreadExecutor(ltf);
	}
	
	public void add(final IExecutorCommand eCommand){
		lockExecutor.submit(new Runnable() {
			@Override
			public void run(){
				synchronized (ProjectCommandExecutor.this) {
					if (eCommand.canStart()){
						runCommand(eCommand);
					} else {
						queue.add(eCommand);
					}
				}
			}
		});
	}
	
	private void runCommand(IExecutorCommand eCommand){
		ListenableFuture future = commandExecutor.submit(eCommand);
		Futures.addCallback(future, new CommandCleaner(eCommand),
			lockExecutor);
	}
	
	private void executePossibleCommands(){
		lockExecutor.submit(new Runnable() {
			@Override
			public void run(){
				synchronized (ProjectCommandExecutor.this) {
					Iterator<IExecutorCommand> iter = queue.iterator();
					while (iter.hasNext()) {
						IExecutorCommand eCommand = iter.next();
						if (eCommand.canStart()) {
							iter.remove();
							runCommand(eCommand);
						}
					}
				}
			}
		});
	}
	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		stop();
	}
	
	public void stop() throws InterruptedException{
		log.info("STARTED Shutting down executors");
		commandExecutor.shutdown();
		commandExecutor.awaitTermination(1, TimeUnit.MINUTES);
		if (!commandExecutor.isTerminated()) {
			log.error("FAILED to shutdown commandExecutor");
		}
		lockExecutor.shutdown();
		lockExecutor.awaitTermination(1, TimeUnit.MINUTES);
		if (!lockExecutor.isTerminated()) {
			log.error("FAILED to shutdown lockExecutor");
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
