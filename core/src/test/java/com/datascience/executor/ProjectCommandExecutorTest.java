package com.datascience.executor;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author konrad
 */
public class ProjectCommandExecutorTest {

	protected int timeout = 200;

	@Test
	public void testAll() throws InterruptedException{
		final Boolean[] canGo = new Boolean[]{false, true, true, false, false};
		IExecutorCommand[] commands = new IExecutorCommand[]{
			new SimpleIExecutor(canGo, 0, 3),
			new SimpleIExecutor(canGo, 1, -1),
			new SimpleIExecutor(canGo, 2, 0),
			new SimpleIExecutor(canGo, 3, -1),
			new SimpleIExecutor(canGo, 4, -1),
		};
		ProjectCommandExecutor executor = new ProjectCommandExecutor();
		Boolean[][] expectations = new Boolean[][]{
			new Boolean[]{false, true, true, false, false},
			new Boolean[]{false, false, true, false, false},
			new Boolean[]{false, false, false, true, false},
			new Boolean[]{false, false, false, false, false},
			new Boolean[]{false, false, false, false, false,},
		};
		for (int i=0;i<canGo.length;i++){
			executor.add(commands[i]);
			Thread.sleep(timeout);
			assertArrayEquals("Error in " + i + " iteration", expectations[i], canGo);
		}
	}

	static class SimpleIExecutor implements IExecutorCommand {
		
		private Boolean[] canGo;
		private int enables;
		private int current;
		
		public SimpleIExecutor(Boolean[] canGo, int current, int enables){
			this.canGo = canGo;
			this.enables = enables;
			this.current = current;
		}

		@Override
		public boolean canStart() {
			return current == -1 || canGo[current];
		}

		@Override
		public void cleanup() {
			if (current != -1) {
				canGo[current] = false;
			}
		}

		@Override
		public void run() {
			if (enables != -1) {
				canGo[enables] = true;
			}
		}
	}
	
	
	@Test
	public void testClosing() throws InterruptedException {
		final Boolean[] tab = new Boolean[]{false, false, false, false, false};
		ProjectCommandExecutor executor = new ProjectCommandExecutor(3);
		executor.add(new DummyIExecutor(tab, 0, 200));
		for (int i=1;i< tab.length;i++){
			executor.add(new DummyIExecutor(tab, i, 0));
		}
		assertArrayEquals("Arrays not equal", new Boolean[]{false, false, false, false, false}, tab);
		executor.stop();
		assertArrayEquals("Arrays not equal", new Boolean[]{true, true, true, true, true}, tab);
	}

	@Test
	public void testClosing2() throws InterruptedException {
		for (int k=0; k< 20; k++){
			final Boolean[] tab = new Boolean[]{false, false, false, false, false};
			ProjectCommandExecutor executor = new ProjectCommandExecutor(3);
			executor.add(new DummyIExecutor(tab, 0, 200));
			for (int i=1;i< tab.length;i++){
				executor.add(new DummyIExecutor(tab, i, 0));
			}
			executor.stop();
			assertEquals(String.valueOf(k), true, tab[0]);
		}
	}
	
	static class DummyIExecutor implements IExecutorCommand {

		int i;
		int sleep;
		Boolean[] tab;
		
		public DummyIExecutor(Boolean[] tab, int i, int sleep){
			this.i = i;
			this.tab = tab;
			this.sleep = sleep;
		}
		
		@Override
		public void run() {
			try {
				Thread.sleep(250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			tab[i] = !tab[i];
		}

		@Override
		public boolean canStart() {
			try {
				Thread.sleep(sleep);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			return true;
		}

		@Override
		public void cleanup() {
		}
	}
	
	@Test
	public void testFailingCommand() throws InterruptedException{
		final Boolean[] ok = new Boolean[]{false};
		ProjectCommandExecutor executor = new ProjectCommandExecutor();
		IExecutorCommand iec = new FailingCommand(ok);
		executor.add(iec);
		Thread.sleep(timeout);
		assertEquals("Failing command wasn't cleaned", true, ok[0]);
	}
	
	static class FailingCommand implements IExecutorCommand {
		
		private Boolean[] ok;

		
		public FailingCommand(Boolean[] ok){
			this.ok = ok;
		}

		@Override
		public boolean canStart() {
			return true;
		}

		@Override
		public void cleanup() {
			ok[0] = true;
		}

		@Override
		public void run() {
			throw new ArtificialError(
				"Don't worry - this is expected one");
		}
	}
	
	static class ArtificialError extends Error {
		
		public ArtificialError(String message) {
			super(message);
		}
	}
	
}
