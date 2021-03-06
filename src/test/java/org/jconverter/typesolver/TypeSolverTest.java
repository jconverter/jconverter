package org.jconverter.typesolver;

import static org.jcategory.category.Key.key;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import org.jcategory.JCategory;
import org.jcategory.category.Key;
import org.junit.Test;

public class TypeSolverTest {

	class ListClass {}
	
	class UnrecognizedObjectExceptionTypeSolver implements TypeSolver<List> {
		@Override
		public Type inferType(List object) {
			throw new UnrecognizedObjectException();
		}
	}
	
	class NonGenericTypeSolver implements TypeSolver {
		@Override
		public Type inferType(Object object) {
			return ListClass.class;
		}
	}
	
	class GenericListTypeSolver implements TypeSolver<List> {
		@Override
		public Type inferType(List object) {
			return ListClass.class;
		}
	}
	
	class FullGenericListTypeSolver implements TypeSolver<List<List<String>>> {
		@Override
		public Type inferType(List object) {
			return ListClass.class;
		}
	}
	
	class SingleBoundListTypeSolver<T extends List> implements TypeSolver<T> {
		@Override
		public Type inferType(T object) {
			return ListClass.class;
		}
	}
	
	class MultipleBoundsListTypeSolver<T extends Iterable & List> implements TypeSolver<T> {
		@Override
		public Type inferType(T object) {
			return ListClass.class;
		}
	}
	
	@Test
	public void testNonGenericListTypeSolver() {
		TypeSolverManager manager = new TypeSolverManagerImpl(new JCategory());
		Key key = key();
		manager.register(key, new NonGenericTypeSolver());
		assertEquals(ListClass.class, manager.inferType(key, new Object()));
		assertEquals(ListClass.class, manager.inferType(key, new ArrayList()));
	}
	
	@Test
	public void testUnrecognizedObjectExceptionTypeSolver() {
		TypeSolverManager manager = new TypeSolverManagerImpl(new JCategory());
		Key key = key();
		manager.register(key, new NonGenericTypeSolver());
		manager.register(key, new UnrecognizedObjectExceptionTypeSolver());
		assertEquals(ListClass.class, manager.inferType(key, new ArrayList()));
	}
	
	@Test
	public void testUnrecognizedObjectExceptionTypeSolver2() {
		TypeSolverManager manager = new TypeSolverManagerImpl(new JCategory());
		Key key = key();
		manager.register(key, new UnrecognizedObjectExceptionTypeSolver());
		try {
			manager.inferType(key(), new Object());
			fail();
		} catch(UnrecognizedObjectException e) {}
		manager.register(key, new NonGenericTypeSolver());
		assertEquals(ListClass.class, manager.inferType(key, new ArrayList()));
	}
	
	@Test
	public void testNoTypeSolver() {
		TypeSolverManager manager = new TypeSolverManagerImpl(new JCategory());
		try {
			manager.inferType(key(), new Object());
			fail();
		} catch(UnrecognizedObjectException e) {}
	}

	@Test
	public void testGenericListTypeSolver() {
		TypeSolverManager manager = new TypeSolverManagerImpl(new JCategory());
		Key key = key();
		manager.register(key, new GenericListTypeSolver());
		try {
			manager.inferType(key, new Object());
			fail();
		} catch(UnrecognizedObjectException e) {}
		assertEquals(ListClass.class, manager.inferType(key, new ArrayList()));
	}
	
	@Test
	public void testFullGenericListTypeSolver() {
		TypeSolverManager manager = new TypeSolverManagerImpl(new JCategory());
		Key key = key();
		manager.register(key, new FullGenericListTypeSolver());
		try {
			manager.inferType(key, new Object());
			fail();
		} catch(UnrecognizedObjectException e) {}
		assertEquals(ListClass.class, manager.inferType(key, new ArrayList()));
	}
	
	@Test
	public void testSingleBoundListTypeSolver() {
		TypeSolverManager manager = new TypeSolverManagerImpl(new JCategory());
		Key key = key();
		manager.register(key, new SingleBoundListTypeSolver());
		try {
			manager.inferType(key, new Object());
			fail();
		} catch(UnrecognizedObjectException e) {}
		assertEquals(ListClass.class, manager.inferType(key, new ArrayList()));
	}
	
	@Test
	public void testMultipleBoundsListTypeSolver() {
		JCategory categorization = new JCategory();
		categorization.forClass(Vector.class);
		TypeSolverManager manager = new TypeSolverManagerImpl(new JCategory());
		Key key = key();
		manager.register(key, new MultipleBoundsListTypeSolver());
		try {
			manager.inferType(key, new Object());
			fail();
		} catch(UnrecognizedObjectException e) {}
		assertEquals(ListClass.class, manager.inferType(key, new Vector())); //trying with a class registered before the bound type solver was added
		assertEquals(ListClass.class, manager.inferType(key, new ArrayList())); //trying with a class registered after the bound type solver was added
		try {
			manager.inferType(key, new HashSet());
			fail();
		} catch(UnrecognizedObjectException e) {}
	}
	
}
