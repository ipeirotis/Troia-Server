package com.datascience.gal.executor;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

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
		commandExecutor = MoreExecutors.listeningDecorator(
			Executors.newFixedThreadPool(numThreads));
		lockExecutor = Executors.newSingleThreadExecutor();
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
			log.log(Level.SEVERE, "Failed executing task: {0} with: {1}",
				new Object[]{className, thrwbl.getLocalizedMessage()});
			log.throwing(className, "run", thrwbl);
			cleanUp();
		}

		private void cleanUp() {
			eCommand.cleanup();
			executePossibleCommands();
		}
	}
}
