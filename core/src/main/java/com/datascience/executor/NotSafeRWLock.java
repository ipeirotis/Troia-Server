package com.datascience.executor;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * This is dummy implementation that provide no thread safety at all
 * but gives logic needed for executor. Use it only when synchronized
 * @Author: konrad
 */
public class NotSafeRWLock implements ReadWriteLock {

	protected volatile int readersCount;
	protected volatile int writersCount;

	protected Lock readLock;
	protected Lock writeLock;

	public NotSafeRWLock(){
		readersCount = 0;
		writersCount = 0;
		readLock = new ReadLock();
		writeLock = new WriteLock();
	}

	@Override
	public Lock readLock() {
		return readLock;
	}

	@Override
	public Lock writeLock() {
		return writeLock;
	}

	protected static abstract class EmptyLock implements Lock {

		@Override
		public void lock() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void lockInterruptibly() throws InterruptedException {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean tryLock(long l, TimeUnit timeUnit) throws InterruptedException {
			throw new UnsupportedOperationException();
		}

		@Override
		public Condition newCondition() {
			throw new UnsupportedOperationException();
		}
	}

	class WriteLock extends NotSafeRWLock.EmptyLock {

		@Override
		public boolean tryLock() {
			boolean success = (writersCount + readersCount) == 0;
			if (success) {
				writersCount = 1;
			}
			return success;
		}

		@Override
		public void unlock() {
			writersCount --;
		}
	}

	class ReadLock extends NotSafeRWLock.EmptyLock {

		@Override
		public boolean tryLock() {
			boolean success = NotSafeRWLock.this.writersCount == 0;
			if (success) {
				readersCount += 1;
			}
			return success;
		}

		@Override
		public void unlock() {
			readersCount --;
		}
	}

}
