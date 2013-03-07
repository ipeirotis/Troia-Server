package com.datascience.scheduler;

import com.datascience.core.base.Data;
import com.datascience.core.base.LObject;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import org.apache.log4j.Logger;

import java.util.concurrent.TimeUnit;

public class CachedScheduler<T> extends Scheduler<T> {

	public static int DEFAULT_PAUSE_TIME = 10;

	private static Logger logger = Logger.getLogger(CachedScheduler.class);

	protected Cache<String, LObject<T>> polled;

	public CachedScheduler(Data<T> data, IPriorityCalculator<T> calculator) {
		this(data, calculator, DEFAULT_PAUSE_TIME);
	}

	public CachedScheduler(Data<T> data, IPriorityCalculator<T> calculator, int pauseTime) {
		super(data, calculator);
		polled = CacheBuilder.newBuilder()
				.expireAfterWrite(pauseTime, TimeUnit.MINUTES)
				.removalListener(getRemovalListener())
				.build();
	}

	@Override
	public LObject<T> nextObject() {
		LObject<T> object = queue.poll();
		if (object != null) {
			polled.put(object.getName(), object);
		}
		return object;
	}

	private RemovalListener<String, LObject<T>> getRemovalListener() {
		return new RemovalListener<String, LObject<T>>() {
			@Override
			public void onRemoval(RemovalNotification<String, LObject<T>> notification) {
				try {
					queue.add(notification.getValue());
				} catch (Exception e) {
					logger.error("CachedScheduler on eviction", e);
				}
			}
		};
	}
}