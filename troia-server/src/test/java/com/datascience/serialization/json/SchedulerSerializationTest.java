package com.datascience.serialization.json;

import com.datascience.core.base.ContValue;
import com.datascience.scheduler.AssignCountPriorityCalculator;
import com.datascience.scheduler.CachedScheduler;
import com.datascience.scheduler.IPriorityCalculator;
import com.datascience.scheduler.Scheduler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.Assert;
import org.junit.Test;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

public class SchedulerSerializationTest {

	private Gson gson;

	public SchedulerSerializationTest() {
		GsonBuilder builder = new GsonBuilder();
		registerSerializer(new TypeToken<Scheduler>(){}.getType(), builder);
		registerDeserializer(new TypeToken<Scheduler>(){}.getType(), builder);
		registerSerializer(new TypeToken<CachedScheduler>(){}.getType(), builder);
		registerDeserializer(new TypeToken<CachedScheduler>(){}.getType(), builder);
		gson = builder.create();
	}

	@Test
	public void schedulerSerializationTest() {
		Scheduler<ContValue> scheduler = new Scheduler<ContValue>();
		IPriorityCalculator<ContValue> calculator = new AssignCountPriorityCalculator<ContValue>();
		scheduler.setUpQueue(calculator);
		String serialized = gson.toJson(scheduler);
		Scheduler<ContValue> deserialized = gson.fromJson(serialized, new TypeToken<Scheduler>(){}.getType());
		Assert.assertEquals(scheduler.getClass(), deserialized.getClass());
		Assert.assertEquals(scheduler.getCalculator().getClass(), deserialized.getCalculator().getClass());
	}

	@Test
	public void cachedSchedulerSerializationTest() {
		CachedScheduler<ContValue> scheduler = new CachedScheduler<ContValue>();
		IPriorityCalculator<ContValue> calculator = new AssignCountPriorityCalculator<ContValue>();
		scheduler.setUpQueue(calculator);
		scheduler.setUpCache(12, TimeUnit.MINUTES);
		String serialized = gson.toJson(scheduler);
		CachedScheduler<ContValue> deserialized = gson.fromJson(serialized, new TypeToken<CachedScheduler>(){}.getType());
		Assert.assertEquals(scheduler.getClass(), deserialized.getClass());
		Assert.assertEquals(scheduler.getCalculator().getClass(), deserialized.getCalculator().getClass());
		Assert.assertEquals(scheduler.getPauseDuration(), deserialized.getPauseDuration());
		Assert.assertEquals(scheduler.getPauseUnit(), deserialized.getPauseUnit());
	}

	private static void registerSerializer(Type type, GsonBuilder builder) {
		builder.registerTypeAdapter(type, new SchedulerJSON.Serializer());
	}

	private static void registerDeserializer(Type type, GsonBuilder builder) {
		builder.registerTypeAdapter(type, new SchedulerJSON.Deserializer());
	}
}
