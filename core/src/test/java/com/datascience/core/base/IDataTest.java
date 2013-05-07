package com.datascience.core.base;

import com.datascience.core.datastoring.kv.KVData;
import com.datascience.core.datastoring.memory.InMemoryData;
import com.datascience.core.datastoring.memory.InMemoryNominalData;
import com.datascience.utils.storage.DefaultSafeKVStorage;
import com.datascience.utils.storage.MemoryKVStorage;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static org.junit.Assert.*;

/**
 * @Author: konrad
 */
@RunWith(Parameterized.class)
public class IDataTest {

	private static Logger logger = Logger.getLogger(IDataTest.class);

	public interface DataCreator{
		IData<String> create();
	}

	final static DataCreator[] DATA_CREATORS = new DataCreator[]{
		new DataCreator(){
			@Override
			public IData<String> create(){
				return new InMemoryData<String>();
			}
		},
		new DataCreator(){
			@Override
			public IData<String> create(){
				InMemoryNominalData data = new InMemoryNominalData();
				data.setCategories(Arrays.asList(new String[]{"cat1", "cat2"}));
				return data;
			}
		},
		new DataCreator(){
			@Override
			public IData<String> create(){
				return new KVData<String>(
						new DefaultSafeKVStorage<Collection<AssignedLabel<String>>>(
								new MemoryKVStorage<Collection<AssignedLabel<String>>>(), logger, ""),
						new DefaultSafeKVStorage<Collection<AssignedLabel<String>>>(
								new MemoryKVStorage<Collection<AssignedLabel<String>>>(), logger, ""),
						new DefaultSafeKVStorage<Collection<LObject<String>>>(
								new MemoryKVStorage<Collection<LObject<String>>>("", new LinkedList<LObject<String>>()), logger, ""),
						new DefaultSafeKVStorage<Collection<LObject<String>>>(
								new MemoryKVStorage<Collection<LObject<String>>>("", new LinkedList<LObject<String>>()), logger, ""),
						new DefaultSafeKVStorage<Collection<LObject<String>>>(
								new MemoryKVStorage<Collection<LObject<String>>>("", new LinkedList<LObject<String>>()), logger, ""),
						new DefaultSafeKVStorage<Collection<Worker<String>>>(
								new MemoryKVStorage<Collection<Worker<String>>>("", new LinkedList<Worker<String>>()), logger, ""));
			}
		}
	};

	public DataCreator dataCreator;
	public IData<String> data;

	public IDataTest(DataCreator dataCreator){
		this.dataCreator = dataCreator;
	}

	@Parameterized.Parameters
	public static Collection<Object[]> instancesToTest() {
		Collection<Object[]> ret = new LinkedList<Object[]>();
		for (DataCreator dc : DATA_CREATORS)
			ret.add(new Object[]{dc});
		return ret;
	}

	@Before
	public void initialize(){
		data = dataCreator.create();
	}

	/**
	 * Testing whether Data properly overrides assigns
	 */
	@Test
	public void testAddAssigns(){
		Worker<String> w1 = data.getOrCreateWorker("Worker1");
		LObject<String> object = data.getOrCreateObject("Object1");
		String label1 = "cat1";
		AssignedLabel<String> al1 = new AssignedLabel<String>(w1, object, label1);
		data.addAssign(al1);
		assertEquals(label1, data.getWorkerAssigns(w1).iterator().next().getLabel());
		assertEquals(label1, data.getAssigns().iterator().next().getLabel());
		assertEquals(label1, data.getAssignsForObject(object).iterator().next().getLabel());
		String label2 = "cat2";
		AssignedLabel<String> al2 = new AssignedLabel<String>(w1, object, label2);
		data.addAssign(al2);
		assertEquals(label2, data.getWorkerAssigns(w1).iterator().next().getLabel());
		assertEquals(label2, data.getAssigns().iterator().next().getLabel());
		assertEquals(label2, data.getAssignsForObject(object).iterator().next().getLabel());
		assertEquals(1, data.getWorkerAssigns(w1).size());
	}

	/**
	 * Testing whether Data properly overrides golds
	 */
	@Test
	public void testAddGold(){
		LObject<String> object1 = data.getOrCreateObject("Object1");
		LObject<String> object2 = data.getOrCreateObject("Object1");
		String label = "cat1";
		object2.setGoldLabel(label);
		data.addObject(object1);
		data.addObject(object2);
		assertEquals(1, data.getObjects().size());
		assertEquals(1, data.getGoldObjects().size());
		assertTrue(data.getGoldObject("Object1").isGold());
		assertEquals(label, data.getObject("Object1").getGoldLabel());
	}

	/**
	 * Testing whether Data properly overrides evaluation data
	 */
	@Test
	public void testAddEvaluation(){
		LObject<String> object1 = data.getOrCreateObject("Object1");
		LObject<String> object2 = data.getOrCreateObject("Object1");
		String label = "cat1";
		object2.setEvaluationLabel(label);
		data.addObject(object1);
		data.addObject(object2);
		assertEquals(1, data.getObjects().size());
		assertEquals(1, data.getEvaluationObjects().size());
		assertTrue(data.getObject("Object1").isEvaluation());
		assertEquals(label, data.getObject("Object1").getEvaluationLabel());
	}

	@Test
	public void testHasAssigns(){
		Worker<String> w1 = data.getOrCreateWorker("Worker1");
		LObject<String> object1 = data.getOrCreateObject("Object1");
		assertFalse(data.hasAssign(object1, w1));
		AssignedLabel<String> al1 = new AssignedLabel<String>(w1, object1, "cat1");
		data.addAssign(al1);
		assertTrue(data.hasAssign(object1, w1));
	}

	@Test
	public void testAddWorker(){
		Worker<String> w1 = data.getOrCreateWorker("Worker1");
		data.addWorker(w1);
		assertNull(data.getWorker("Worker2"));
		assertNotNull(data.getWorker("Worker1"));
		Worker<String> w2 = new Worker<String>("Worker2");
		data.addWorker(w2);
		assertNotNull(data.getWorker("Worker2"));
	}

	@Test
	public void testAddObject(){
		LObject<String> object1 = data.getOrCreateObject("Object1");
		data.addObject(object1);
		assertNull(data.getObject("Object2"));
		assertNotNull(data.getObject("Object1"));
		LObject<String> o2 = new LObject<String>("Object2");
		data.addObject(o2);
		assertNotNull(data.getObject("Object2"));
	}

	@Test
	public void testMarkingObjectsAsGold(){
		LObject<String> object1 = data.getOrCreateObject("Object1");
		data.addObject(object1);
		assertFalse(object1.isGold());
		assertEquals(0, data.getGoldObjects().size());
		data.markObjectAsGold(object1, "cat1");
		assertTrue(object1.isGold());
		assertEquals(1, data.getGoldObjects().size());
		assertEquals(object1, data.getGoldObject("Object1"));
	}
}
