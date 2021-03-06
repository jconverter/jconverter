package org.jconverter.factory;

import static org.jcategory.category.Key.key;
import static org.jconverter.JConverter.jConverter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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
import org.jcategory.JCategory;
import org.jcategory.category.Key;
import org.junit.Test;

public class FactoryTest {

	class NonGenericFactory implements Factory {
		@Override
		public Object instantiate(Type type) {
			return new ArrayList();
		}
	}
	
	class GenericCollectionFactory implements Factory<Collection> {
		@Override
		public Collection instantiate(Type type) {
			return new ArrayList();
		}
	}
	
	class BoundedCollectionFactory<T extends Collection> implements Factory<T> {
		@Override
		public T instantiate(Type type) {
			return (T) new ArrayList(); //this is not really correct (the type is not considered), but just for the sake of testing ...
		}
	}
	
	@Test
	public void testNoFactory() {
		FactoryManager factoryManager = new FactoryManagerImpl(new JCategory());
		Key key = key();
		try {
			factoryManager.instantiate(key, Collection.class);
			fail();
		} catch(Exception e) {}
	}
	
	@Test
	public void testNoGenericFactory() {
		FactoryManager factoryManager = new FactoryManagerImpl(new JCategory());
		Key key = key();
		try {
			factoryManager.register(key, new NonGenericFactory());
			fail();
		} catch(Exception e) {}
	}
	
	@Test
	public void testGenericCollectionFactory() {
		FactoryManager factoryManager = new FactoryManagerImpl(new JCategory());
		Key key = key();
		factoryManager.register(key, new GenericCollectionFactory());
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
	public void testBoundedCollectionFactory() {
		FactoryManager factoryManager = new FactoryManagerImpl(new JCategory());
		Key key = key();
		factoryManager.register(key, new BoundedCollectionFactory());
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
		JConverter jconverter = jConverter();
		assertNotNull(jconverter.instantiate(List.class));
		assertNotNull(jconverter.instantiate(Iterable.class));
		assertNotNull(jconverter.instantiate(ArrayList.class));
		assertNotNull(jconverter.instantiate(Set.class));
		assertNotNull(jconverter.instantiate(Map.class));
		assertNotNull(jconverter.instantiate(Deque.class));
		assertNotNull(jconverter.instantiate(Calendar.class));
	}
	
}
