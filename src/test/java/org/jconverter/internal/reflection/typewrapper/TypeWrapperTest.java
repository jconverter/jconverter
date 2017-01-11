package org.jconverter.internal.reflection.typewrapper;

import static org.jconverter.internal.reflection.typewrapper.TypeWrapper.wrap;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jconverter.internal.reflection.FixtureReflectionTests;
import org.jconverter.internal.reflection.typewrapper.FixtureTypeWrapper.B;
import org.junit.Test;

import com.google.common.base.Supplier;
import com.google.common.reflect.TypeToken;

public class TypeWrapperTest {

	@Test
	public void testFindTypeVariables() {
		Type type = B.class.getGenericSuperclass(); //A<java.util.Map<Z, Y>, java.lang.String, Y>
		//System.out.println(type);
		
		TypeWrapper typeWrapper = wrap(type);
		List<TypeVariable> namedTypeVariables = typeWrapper.getNamedTypeVariables(); //two type variables: Z,Y
		//System.out.println("Named type variables: " + namedTypeVariables);
		assertEquals(namedTypeVariables.size(), 2);
		TypeVariable variableZ = namedTypeVariables.get(0);
		TypeVariable variableY = namedTypeVariables.get(1);
		assertEquals(variableZ.getName(), "Z");
		assertEquals(variableY.getName(), "Y");
		
		
		Map<TypeVariable, Type> map = new HashMap<TypeVariable, Type>();
		map.put(variableZ, String.class); //replace Z by String
		
		type = typeWrapper.bindVariables(map); //new bound type
		//System.out.println(type);
		typeWrapper = wrap(type);
		namedTypeVariables = typeWrapper.getNamedTypeVariables(); //only Y remains, Z was replaced by String
		assertEquals(namedTypeVariables.size(), 1);
		variableY = namedTypeVariables.get(0);
		assertEquals(variableY.getName(), "Y");
		
		
		map = new HashMap<TypeVariable, Type>();
		map.put(variableY, String.class); //replace Y by String
		
		type = typeWrapper.bindVariables(map);
		//System.out.println(type);
		typeWrapper = wrap(type);
		namedTypeVariables = typeWrapper.getNamedTypeVariables();
		assertEquals(namedTypeVariables.size(), 0); //no type variables left
	}
	
	public void testActualTypeArguments() {
		TypeWrapper typeWrapper = TypeWrapper.wrap(new TypeToken<Supplier<Integer>>(){}.getType());
		assertEquals(1, typeWrapper.getActualTypeArguments().length);
		assertEquals(new TypeToken<Supplier<Integer>>(){}.getType(), typeWrapper.getActualTypeArguments()[0]);
		
		typeWrapper = TypeWrapper.wrap(new TypeToken<Supplier>(){}.getType());
		assertEquals(0, typeWrapper.getActualTypeArguments().length);
	}
	
	/**
	 * Testing border cases that gave problems on the past.
	 */
	@Test
	public void testAsDirectImplementorType() {
		Supplier<Integer> supplier = new Supplier<Integer>() {
			@Override
			public Integer get() {
				return null;
			}
		};
		TypeWrapper customSupplierTypeWrapper = TypeWrapper.wrap(supplier.getClass());
		assertEquals(new TypeToken<Supplier<Integer>>(){}.getType(), customSupplierTypeWrapper.as(Supplier.class).wrappedType);
		
		TypeWrapper supplierTypeWrapper = TypeWrapper.wrap(Supplier.class);
		assertEquals(supplier.getClass(), supplierTypeWrapper.as(supplier.getClass()).getWrappedType());
	}
	
	@Test
	public void testAsDirectImplementorTypeWithoutGenerics() {
		Supplier supplier = new Supplier() {
			@Override
			public Object get() {
				return null;
			}
		};
		TypeWrapper customSupplierTypeWrapper = TypeWrapper.wrap(supplier.getClass());
		assertEquals(new TypeToken<Supplier>(){}.getType(), customSupplierTypeWrapper.as(Supplier.class).wrappedType);

		TypeWrapper supplierTypeWrapper = TypeWrapper.wrap(Supplier.class);
		assertEquals(supplier.getClass(), supplierTypeWrapper.as(supplier.getClass()).getWrappedType());
	}
	
	@Test
	public void testAsDirectImplementorTypeBoundVariable() {
		class S<T extends Collection> implements Supplier<T> {
			@Override
			public T get() {
				return null;
			}
		}
		TypeWrapper customSupplierTypeWrapper = TypeWrapper.wrap(S.class);
		TypeWrapper typeWrapper = customSupplierTypeWrapper.as(Supplier.class);
		assertEquals(1, typeWrapper.getActualTypeArguments().length);
		assertEquals(1, ((TypeVariable)typeWrapper.getActualTypeArguments()[0]).getBounds().length);
		
		typeWrapper = TypeWrapper.wrap(Supplier.class).as(S.class);
		assertEquals(S.class, typeWrapper.getWrappedType());
	}
	
	
	@Test
	public void testAsType() {
		
		Type unboundCollectionType = Collection.class;
		Type boundCollectionType = new TypeToken<Collection<String>>(){}.getType();
		Type unboundArrayListType = ArrayList.class;
		Type boundArrayListType = new TypeToken<ArrayList<String>>(){}.getType();
		
		Type boundCollectionToBoundArrayListType = wrap(boundCollectionType).asType(boundArrayListType);
		assertEquals(boundArrayListType, boundCollectionToBoundArrayListType);
		
		Type boundArrayListToBoundCollectionType = wrap(boundArrayListType).asType(boundCollectionType);
		assertEquals(boundCollectionType, boundArrayListToBoundCollectionType);
		
		Type unboundCollectionToUnboundArrayListType = wrap(unboundCollectionType).asType(unboundArrayListType);
		assertEquals(unboundArrayListType, unboundCollectionToUnboundArrayListType);
		
		Type unboundArrayListToUnboundCollectionType = wrap(unboundArrayListType).asType(unboundCollectionType);
		assertEquals(unboundCollectionType, unboundArrayListToUnboundCollectionType);
		
		
		
		Type boundCollectionToUnboundArrayListType = wrap(boundCollectionType).asType(unboundArrayListType);
		assertEquals(boundArrayListType, boundCollectionToUnboundArrayListType);
		
		Type unboundArrayListToBoundCollectionType = wrap(unboundArrayListType).asType(boundCollectionType);
		assertEquals(boundCollectionType, unboundArrayListToBoundCollectionType);
		
		Type unboundCollectionToBoundArrayListType = wrap(unboundCollectionType).asType(boundArrayListType);
		assertEquals(boundArrayListType, unboundCollectionToBoundArrayListType);
		
		Type boundArrayListToUnboundCollectionType = wrap(boundArrayListType).asType(unboundCollectionType);
		assertEquals(boundCollectionType, boundArrayListToUnboundCollectionType);
		
		
		
		Type boundCollectionToUnboundCollectionType = wrap(boundCollectionType).asType(unboundCollectionType);
		assertEquals(boundCollectionType, boundCollectionToUnboundCollectionType);
		
		Type unboundCollectionToBoundCollectionType = wrap(unboundCollectionType).asType(boundCollectionType);
		assertEquals(boundCollectionType, unboundCollectionToBoundCollectionType);
		
		Type boundCollectionToBoundCollectionType = wrap(boundCollectionType).asType(boundCollectionType);
		assertEquals(boundCollectionType, boundCollectionToBoundCollectionType);
		
		Type unboundCollectionToUnboundCollectionType = wrap(unboundCollectionType).asType(unboundCollectionType);
		assertEquals(unboundCollectionType, unboundCollectionToUnboundCollectionType);
		
		
		
		Type boundArrayListToUnboundArrayListType = wrap(boundArrayListType).asType(unboundArrayListType);
		assertEquals(boundArrayListType, boundArrayListToUnboundArrayListType);
		
		Type unboundArrayListToBoundArrayListType = wrap(unboundArrayListType).asType(boundArrayListType);
		assertEquals(boundArrayListType, unboundArrayListToBoundArrayListType);
		
		Type boundArrayListToBoundArrayListType = wrap(boundArrayListType).asType(boundArrayListType);
		assertEquals(boundArrayListType, boundArrayListToBoundArrayListType);
		
		Type unboundArrayListToUnboundArrayListType = wrap(unboundArrayListType).asType(unboundArrayListType);
		assertEquals(unboundArrayListType, unboundArrayListToUnboundArrayListType);
		
	
		
		Type boundCollectionToObjectType = wrap(boundCollectionType).asType(Object.class);
		assertEquals(Object.class, boundCollectionToObjectType);
		
		Type objectToBoundCollectionType = wrap(Object.class).asType(boundCollectionType);
		assertEquals(boundCollectionType, objectToBoundCollectionType);
		
		Type unboundCollectionToObjectType = wrap(unboundCollectionType).asType(Object.class);
		assertEquals(Object.class, unboundCollectionToObjectType);
		
		Type objectToUnboundCollectionType = wrap(Object.class).asType(unboundCollectionType);
		assertEquals(unboundCollectionType, objectToUnboundCollectionType);
		
		
		
		Type boundArrayListToObjectType = wrap(boundArrayListType).asType(Object.class);
		assertEquals(Object.class, boundArrayListToObjectType);
		
		Type objectToBoundArrayListType = wrap(Object.class).asType(boundArrayListType);
		assertEquals(boundArrayListType, objectToBoundArrayListType);
		
		Type unboundArrayListToObjectType = wrap(unboundArrayListType).asType(Object.class);
		assertEquals(Object.class, unboundArrayListToObjectType);
		
		Type objectToUnboundArrayListType = wrap(Object.class).asType(unboundArrayListType);
		assertEquals(unboundArrayListType, objectToUnboundArrayListType);
		
		
		assertEquals(Object.class, wrap(Object.class).asType(Object.class));
	}

	@Test
	public void testAsTypeArray() {
		Type boundCollectionArrayType = new TypeToken<Collection<String>[]>(){}.getType();
		Type unboundCollectionArrayType = new TypeToken<Collection[]>(){}.getType();
		Type boundArrayListArrayType = new TypeToken<ArrayList<String>[]>(){}.getType();
		Type unboundArrayListArrayType = new TypeToken<ArrayList[]>(){}.getType();
		
		Type boundCollectionArrayToBoundCollectionArrayType = wrap(boundCollectionArrayType).asType(boundCollectionArrayType);
		assertEquals(boundCollectionArrayType, boundCollectionArrayToBoundCollectionArrayType);
		
		Type boundCollectionArrayToUnboundCollectionArrayType = wrap(boundCollectionArrayType).asType(unboundCollectionArrayType);
		assertEquals(boundCollectionArrayType, boundCollectionArrayToUnboundCollectionArrayType);
		
		Type boundCollectionArrayToBoundArrayListArrayType = wrap(boundCollectionArrayType).asType(boundArrayListArrayType);
		assertEquals(boundArrayListArrayType, boundCollectionArrayToBoundArrayListArrayType);
		
		Type boundCollectionArrayToUnboundArrayListArrayType = wrap(boundCollectionArrayType).asType(unboundArrayListArrayType);
		assertEquals(boundArrayListArrayType, boundCollectionArrayToUnboundArrayListArrayType);
	}
	
	@Test
	public void testGetRawClass() {
		Type boundCollectionType = new TypeToken<Collection<String>>(){}.getType();
		Type unboundCollectionType = new TypeToken<Collection>(){}.getType();
		Type boundCollectionArrayType = new TypeToken<Collection<String>[]>(){}.getType();
		Type unboundCollectionArrayType = new TypeToken<Collection[]>(){}.getType();
		
		assertEquals(wrap(boundCollectionType).getRawClass(), wrap(unboundCollectionType).getRawClass());
		assertEquals(wrap(boundCollectionArrayType).getRawClass(), wrap(unboundCollectionArrayType).getRawClass());
	}
	
	@Test
	public void testIsNotAssignable() {
		Type parameterizedCollectionType1 = new TypeToken<Collection<String>>(){}.getType();
		Type parameterizedCollectionType2 = new TypeToken<Collection<Number>>(){}.getType();
		
		assertFalse(wrap(parameterizedCollectionType1).isAssignableFrom(parameterizedCollectionType2));
		assertFalse(wrap(parameterizedCollectionType1).isWeakAssignableFrom(parameterizedCollectionType2));
	}
	
	@Test
	public void testIsAssignableWithRawType() {
		Type parameterizedCollectionType = new TypeToken<Collection<String>>(){}.getType();
		Type rawCollectionType = new TypeToken<Collection>(){}.getType();
		Type parameterizedCollectionArrayType = new TypeToken<Collection<String>[]>(){}.getType();
		Type rawCollectionArrayType = new TypeToken<Collection[]>(){}.getType();
		Type parameterizedCollectionArray2Type = new TypeToken<Collection<String>[][]>(){}.getType();
		Type rawCollectionArray2Type = new TypeToken<Collection[][]>(){}.getType();
		Class<?> objectArrayClass = new Object[]{}.getClass();
		
		assertFalse(wrap(parameterizedCollectionType).isAssignableFrom(rawCollectionType));
		assertTrue(wrap(parameterizedCollectionType).isWeakAssignableFrom(rawCollectionType));
		
		assertTrue(wrap(rawCollectionType).isAssignableFrom(parameterizedCollectionType));
		assertTrue(wrap(rawCollectionType).isWeakAssignableFrom(parameterizedCollectionType));
		
		
		assertFalse(wrap(parameterizedCollectionArrayType).isAssignableFrom(rawCollectionArrayType));
		assertTrue(wrap(parameterizedCollectionArrayType).isWeakAssignableFrom(rawCollectionArrayType));
		
		assertTrue(wrap(rawCollectionArrayType).isAssignableFrom(parameterizedCollectionArrayType));
		assertTrue(wrap(rawCollectionArrayType).isWeakAssignableFrom(parameterizedCollectionArrayType));
		
		
		assertTrue(wrap(Object.class).isAssignableFrom(rawCollectionType));
		assertTrue(wrap(Object.class).isWeakAssignableFrom(rawCollectionType));
		
		assertTrue(wrap(Object.class).isAssignableFrom(parameterizedCollectionType));
		assertTrue(wrap(Object.class).isWeakAssignableFrom(parameterizedCollectionType));
		
		assertTrue(wrap(Object.class).isAssignableFrom(rawCollectionArrayType));
		assertTrue(wrap(Object.class).isWeakAssignableFrom(rawCollectionArrayType));
		
		assertTrue(wrap(Object.class).isAssignableFrom(parameterizedCollectionArrayType));
		assertTrue(wrap(Object.class).isWeakAssignableFrom(parameterizedCollectionArrayType));
		
		
		assertTrue(wrap(objectArrayClass).isAssignableFrom(rawCollectionArrayType));
		assertTrue(wrap(objectArrayClass).isWeakAssignableFrom(rawCollectionArrayType));
		
		assertTrue(wrap(objectArrayClass).isAssignableFrom(parameterizedCollectionArrayType));
		assertTrue(wrap(objectArrayClass).isWeakAssignableFrom(parameterizedCollectionArrayType));
		
		assertTrue(wrap(objectArrayClass).isAssignableFrom(rawCollectionArray2Type));
		assertTrue(wrap(objectArrayClass).isWeakAssignableFrom(rawCollectionArray2Type));
		
		assertTrue(wrap(objectArrayClass).isAssignableFrom(parameterizedCollectionArray2Type));
		assertTrue(wrap(objectArrayClass).isWeakAssignableFrom(parameterizedCollectionArray2Type));
	}
	
	@Test
	public void testIsAssignableWithWildCardType() {
		Type parameterizedCollectionType = new TypeToken<Collection<String>>(){}.getType();
		Type wildCardCollectionType = new TypeToken<Collection<?>>(){}.getType();
		Type parameterizedCollectionArrayType = new TypeToken<Collection<String>[]>(){}.getType();
		Type wildCardCollectionArrayType = new TypeToken<Collection<?>[]>(){}.getType();
		Type parameterizedCollectionArray2Type = new TypeToken<Collection<String>[][]>(){}.getType();
		Type wildCardCollectionArray2Type = new TypeToken<Collection<?>[][]>(){}.getType();
		Class<?> objectArrayClass = new Object[]{}.getClass();
		
		assertFalse(wrap(parameterizedCollectionType).isAssignableFrom(wildCardCollectionType));
		assertTrue(wrap(parameterizedCollectionType).isWeakAssignableFrom(wildCardCollectionType));
		
		assertTrue(wrap(wildCardCollectionType).isAssignableFrom(parameterizedCollectionType));
		assertTrue(wrap(wildCardCollectionType).isWeakAssignableFrom(parameterizedCollectionType));
		
		
		assertFalse(wrap(parameterizedCollectionArrayType).isAssignableFrom(wildCardCollectionArrayType));
		assertTrue(wrap(parameterizedCollectionArrayType).isWeakAssignableFrom(wildCardCollectionArrayType));
		
		assertTrue(wrap(wildCardCollectionArrayType).isAssignableFrom(parameterizedCollectionArrayType));
		assertTrue(wrap(wildCardCollectionArrayType).isWeakAssignableFrom(parameterizedCollectionArrayType));
		
		
		assertTrue(wrap(Object.class).isAssignableFrom(wildCardCollectionType));
		assertTrue(wrap(Object.class).isWeakAssignableFrom(wildCardCollectionType));
		
		assertTrue(wrap(Object.class).isAssignableFrom(parameterizedCollectionType));
		assertTrue(wrap(Object.class).isWeakAssignableFrom(parameterizedCollectionType));
		
		assertTrue(wrap(Object.class).isAssignableFrom(wildCardCollectionArrayType));
		assertTrue(wrap(Object.class).isWeakAssignableFrom(wildCardCollectionArrayType));
		
		assertTrue(wrap(Object.class).isAssignableFrom(parameterizedCollectionArrayType));
		assertTrue(wrap(Object.class).isWeakAssignableFrom(parameterizedCollectionArrayType));
		
		
		assertTrue(wrap(objectArrayClass).isAssignableFrom(wildCardCollectionArrayType));
		assertTrue(wrap(objectArrayClass).isWeakAssignableFrom(wildCardCollectionArrayType));
		
		assertTrue(wrap(objectArrayClass).isAssignableFrom(parameterizedCollectionArrayType));
		assertTrue(wrap(objectArrayClass).isWeakAssignableFrom(parameterizedCollectionArrayType));
		
		assertTrue(wrap(objectArrayClass).isAssignableFrom(wildCardCollectionArray2Type));
		assertTrue(wrap(objectArrayClass).isWeakAssignableFrom(wildCardCollectionArray2Type));
		
		assertTrue(wrap(objectArrayClass).isAssignableFrom(parameterizedCollectionArray2Type));
		assertTrue(wrap(objectArrayClass).isWeakAssignableFrom(parameterizedCollectionArray2Type));
	}
	
	@Test
	public void testIsAssignableWithWildCardType2() {
		Type wildCardCollectionType1 = new TypeToken<Collection<?>>(){}.getType();
		Type wildCardCollectionType2 = new TypeToken<Collection<?>>(){}.getType();
		
		assertTrue(wrap(wildCardCollectionType1).isAssignableFrom(wildCardCollectionType2));
		assertTrue(wrap(wildCardCollectionType1).isWeakAssignableFrom(wildCardCollectionType2));
	}
	
	@Test
	public void testVariableTypeIsAssignable() {
		TypeWrapper unboundVariableTypeWrapper = TypeWrapper.wrap(FixtureReflectionTests.unboundTypeVariable);
		TypeWrapper boundVariableTypeWrapper = TypeWrapper.wrap(FixtureReflectionTests.boundTypeVariable);
		
		assertFalse(unboundVariableTypeWrapper.isAssignableFrom(Object.class));
		assertTrue(unboundVariableTypeWrapper.isWeakAssignableFrom(Object.class));
		
		assertFalse(boundVariableTypeWrapper.isAssignableFrom(Object.class));
		assertFalse(boundVariableTypeWrapper.isWeakAssignableFrom(Object.class));
		assertTrue(boundVariableTypeWrapper.isWeakAssignableFrom(List.class));
		assertTrue(boundVariableTypeWrapper.isWeakAssignableFrom(ArrayList.class));
		
	}
}
