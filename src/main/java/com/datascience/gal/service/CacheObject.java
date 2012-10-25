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

package com.datascience.gal.service;

import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class CacheObject<PayloadType> {




	/**
	 * Object that is held in cache
	 */
	private PayloadType payload;

	/**
	 * Writer that is currently modifing payload
	 */
	private Object writerLockOwner;


	boolean isWriteLockedBy(Object source) {
		return source==this.writerLockOwner;
	}

	boolean isReadLockedBy(Object source) {
		return this.readerLockOwners.contains(source);
	}


	private static final int READER_WRITER_SLEEP_PERIOD = 10;

	/**
	 * Readers that are reading payload
	 */
	private List<Object> readerLockOwners;

	/**
	 * Readers that are waiting for access to payload
	 */
	private Queue<Object> readersQueue;

	/**
	 * Writers that are waiting for access to payload
	 */
	private Queue<Object> writersQueue;


	public CacheObject(PayloadType payload) {
		this.payload = payload;
		this.writerLockOwner = null;
		this.readerLockOwners = new ArrayList<Object>();
		this.readersQueue = new LinkedBlockingQueue<Object>();
		this.writersQueue = new LinkedBlockingQueue<Object>();
	}

	/**
	 * @return Object that is held in cache
	 */
	public PayloadType getPayloadForReadOnly(Object reader) {
		boolean wait = false;
		synchronized(this.payload) {
			if(this.writerLockOwner==null&&this.writersQueue.size()==0) {
				this.readerLockOwners.add(reader);
			} else {
				this.readersQueue.add(reader);
				wait = true;
			}
		}
		while(wait) {
			if(this.readerLockOwners.contains(reader)) {
				wait = false;
			} else {
				try {
					Thread.sleep(READER_WRITER_SLEEP_PERIOD);
				} catch(InterruptedException e) {
					//FIXME THIS MUST DO SOMETHING
				}
			}
		}
		logger.debug("Returning payload for read-write mode.");
		return payload;
	}

	/**
	 * @return Object that is held in cache
	 */
	public PayloadType getPayloadForEditing(Object writer) {
		boolean wait = false;
		synchronized(this.payload) {
			if(this.writerLockOwner==null&&this.readerLockOwners.size()==0) {
				this.writerLockOwner=writer;
			} else {
				if(this.writersQueue.size()==0) {
					this.writerLockOwner=writer;
				} else {
					this.writersQueue.add(writer);
				}
			}
		}
		while(wait) {
			if(this.writerLockOwner==writer) {
				wait=true;
			} else {
				try {
					Thread.sleep(READER_WRITER_SLEEP_PERIOD);
				} catch(InterruptedException e) {
					//FIXME THIS MUST DO SOMETHING
				}
			}
		}
		logger.debug("Returning payload for read-write mode.");
		return payload;
	}

	public void setPayload(PayloadType payload,Object source) {
		if(source==this.writerLockOwner) {
			this.payload=payload;
		}
	}

	public void relasePaloyadLock(Object owner) {
		if(owner==this.writerLockOwner) {
			this.writerLockOwner = this.writersQueue.poll();
			if(this.writerLockOwner==null) {
				while(this.readersQueue.size()>0) {
					this.readerLockOwners.add(this.readersQueue.poll());
				}
			}
			logger.debug("Relasing read-write lock.");
		} else if(this.readerLockOwners.contains(owner)) {
			this.readerLockOwners.remove(owner);
			if(this.readerLockOwners.size()==0) {
				this.writerLockOwner = this.writersQueue.poll();
			}
			logger.debug("Relasing read-only lock.");
		}
	}

	private static Logger logger = Logger.getLogger(CacheObject.class);

}
