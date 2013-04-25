package com.datascience.executor;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;

/**
 *
 * @author konrad
 */
public abstract class SynchronizedCommand implements IExecutorCommand{

	private Lock lock;
	
	public SynchronizedCommand(ReadWriteLock rwLock, boolean modifies){
		adjustLock(rwLock, modifies);
	}
	
	private void adjustLock(ReadWriteLock rwLock, boolean modifies){
		if (modifies) {
			lock = rwLock.writeLock();
		} else {
			lock = rwLock.readLock();
		}
	}
	
	@Override
	public boolean canStart() {
		return lock.tryLock();
	}

	@Override
	public void cleanup() {
		lock.unlock();
	}
}