package org.jconverter.instantiation;

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
import org.jgum.JGum;
import org.junit.Test;

public class InstanceCreatorTest {

	class NonGenericInstanceCreator implements InstanceCreator {
		@Override
		public Object instantiate(Type type) {
			return new ArrayList();
		}
	}
	
	class GenericCollectionInstanceCreator implements InstanceCreator<Collection> {
		@Override
		public Collection instantiate(Type type) {
			return new ArrayList();
		}
	}
	
	class BoundedCollectionInstanceCreator<T extends Collection> implements InstanceCreator<T> {
		@Override
		public T instantiate(Type type) {
			return (T) new ArrayList(); //this is not really correct (the type is not considered), but just for the sake of testing ...
		}
	}
	
	@Test
	public void testNoInstanceCreator() {
		InstantiationManager instantiationManager = new JGumInstantiationManager(new JGum());
		Object key = new Object();
		try {
			instantiationManager.instantiate(key, Collection.class);
			fail();
		} catch(Exception e) {}
	}
	
	@Test
	public void testNoGenericInstanceCreator() {
		InstantiationManager instantiationManager = new JGumInstantiationManager(new JGum());
		Object key = new Object();
		try {
			instantiationManager.register(key, new NonGenericInstanceCreator());
			fail();
		} catch(Exception e) {}
	}
	
	@Test
	public void testGenericCollectionInstanceCreator() {
		InstantiationManager instantiationManager = new JGumInstantiationManager(new JGum());
		Object key = new Object();
		instantiationManager.register(key, new GenericCollectionInstanceCreator());
		assertEquals(ArrayList.class, instantiationManager.instantiate(key, Collection.class).getClass());
		try {
			instantiationManager.instantiate(key, List.class);
			fail();
		} catch(Exception e) {}
		try {
			instantiationManager.instantiate(key, ArrayList.class);
			fail();
		} catch(Exception e) {}
	}
	
	@Test
	public void testBoundedCollectionInstanceCreator() {
		InstantiationManager instantiationManager = new JGumInstantiationManager(new JGum());
		Object key = new Object();
		instantiationManager.register(key, new BoundedCollectionInstanceCreator());
		assertEquals(ArrayList.class, instantiationManager.instantiate(key, Collection.class).getClass());
		assertEquals(ArrayList.class, instantiationManager.instantiate(key, List.class).getClass());
		assertEquals(ArrayList.class, instantiationManager.instantiate(key, ArrayList.class).getClass());
		try {
			instantiationManager.instantiate(key, Iterable.class);
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
