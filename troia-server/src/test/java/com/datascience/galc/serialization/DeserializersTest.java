package com.datascience.galc.serialization;

import com.datascience.galc.Datum;
import com.datascience.galc.DatumCont;
import com.datascience.galc.Worker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Micha? Borysiak
 */
public class DeserializersTest {
	
	private Gson gson;
	
	private Type workerType = new TypeToken<Worker>(){}.getType();
	private Type datumType = new TypeToken<Datum>(){}.getType();
	
	public DeserializersTest() {
		GsonBuilder builder = new GsonBuilder();
		builder.registerTypeAdapter(datumType, new DatumDeserializer());
		builder.registerTypeAdapter(workerType, new WorkerDeserializer());
		gson = builder.create();
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	@Test
	public void datumDerializerTest() {
		Datum datum = new DatumCont("datum");
		String json = gson.toJson(datum);
		System.out.println(gson + " " + json + "   " + datumType);
		Datum deserialized = gson.fromJson(json, datumType);
		assertEquals(datum, deserialized);
	}
	
	@Test
	public void workerDerializerTest() {
		Worker worker = new Worker("datum");
		String json = gson.toJson(worker);
		Datum deserialized = gson.fromJson(json, workerType);
		assertEquals(worker, deserialized);
	}
}
