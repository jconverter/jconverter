package org.jconverter.factory;

import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jconverter.JConverter;
import org.jconverter.factory.Factory;
import org.jconverter.factory.FactoryManager;
import org.jconverter.factory.JGumFactoryManager;
import org.jgum.JGum;
import org.junit.Test;

public class FactoryTest {

	class NonGenericInstanceCreator implements Factory {
		@Override
		public Object instantiate(Type type) {
			return new ArrayList();
		}
	}
	
	class GenericCollectionInstanceCreator implements Factory<Collection> {
		@Override
		public Collection instantiate(Type type) {
			return new ArrayList();
		}
	}
	
	class BoundedCollectionInstanceCreator<T extends Collection> implements Factory<T> {
		@Override
		public T instantiate(Type type) {
			return (T) new ArrayList(); //this is not really correct (the type is not considered), but just for the sake of testing ...
		}
	}
	
	@Test
	public void testNoInstanceCreator() {
		FactoryManager factoryManager = new JGumFactoryManager(new JGum());
		Object key = new Object();
		try {
			factoryManager.instantiate(key, Collection.class);
			fail();
		} catch(Exception e) {}
	}
	
	@Test
	public void testNoGenericInstanceCreator() {
		FactoryManager factoryManager = new JGumFactoryManager(new JGum());
		Object key = new Object();
		try {
			factoryManager.register(key, new NonGenericInstanceCreator());
			fail();
		} catch(Exception e) {}
	}
	
	@Test
	public void testGenericCollectionInstanceCreator() {
		FactoryManager factoryManager = new JGumFactoryManager(new JGum());
		Object key = new Object();
		factoryManager.register(key, new GenericCollectionInstanceCreator());
		assertEquals(ArrayList.class, factoryManager.instantiate(key, Collection.class).getClass());
		try {
			factoryManager.instantiate(key, List.class);
			fail();
		} catch(Exception e) {}
		try {
			factoryManager.instantiate(key, ArrayList.class);
			fail();
		} catch(Exception e) {}
	}
	
	@Test
	public void testBoundedCollectionInstanceCreator() {
		FactoryManager factoryManager = new JGumFactoryManager(new JGum());
		Object key = new Object();
		factoryManager.register(key, new BoundedCollectionInstanceCreator());
		assertEquals(ArrayList.class, factoryManager.instantiate(key, Collection.class).getClass());
		assertEquals(ArrayList.class, factoryManager.instantiate(key, List.class).getClass());
		assertEquals(ArrayList.class, factoryManager.instantiate(key, ArrayList.class).getClass());
		try {
			factoryManager.instantiate(key, Iterable.class);
			fail();
		} catch(Exception e) {}
	}
	
	@Test
	public void testDefaultInstantiations() {
		JConverter jconverter = new JConverter();
		assertNotNull(jconverter.instantiate(List.class));
		assertNotNull(jconverter.instantiate(Iterable.class));
		assertNotNull(jconverter.instantiate(ArrayList.class));
		assertNotNull(jconverter.instantiate(Set.class));
		assertNotNull(jconverter.instantiate(Map.class));
		assertNotNull(jconverter.instantiate(Deque.class));
		assertNotNull(jconverter.instantiate(Calendar.class));
	}
	
}