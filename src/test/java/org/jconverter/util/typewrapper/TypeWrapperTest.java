package org.jconverter.util.typewrapper;

import static org.jconverter.util.typewrapper.TypeWrapper.wrap;
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

import org.jconverter.util.FixtureReflectionTests;
import org.junit.Assert;
import org.junit.Test;

import com.google.common.base.Supplier;
import com.google.common.reflect.TypeToken;

public class TypeWrapperTest {

	@Test
	public void testFindTypeVariables() {
		Type type = FixtureTypeWrapper.B.class.getGenericSuperclass(); //A<java.util.Map<Z, Y>, java.lang.String, Y>
		//System.out.println(type);
		
		TypeWrapper typeWrapper = TypeWrapper.wrap(type);
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
		typeWrapper = TypeWrapper.wrap(type);
		namedTypeVariables = typeWrapper.getNamedTypeVariables(); //only Y remains, Z was replaced by String
		assertEquals(namedTypeVariables.size(), 1);
		variableY = namedTypeVariables.get(0);
		assertEquals(variableY.getName(), "Y");
		
		
		map = new HashMap<TypeVariable, Type>();
		map.put(variableY, String.class); //replace Y by String
		
		type = typeWrapper.bindVariables(map);
		//System.out.println(type);
		typeWrapper = TypeWrapper.wrap(type);
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
		
		Type boundCollectionToBoundArrayListType = TypeWrapper.wrap(boundCollectionType).asType(boundArrayListType);
		assertEquals(boundArrayListType, boundCollectionToBoundArrayListType);
		
		Type boundArrayListToBoundCollectionType = TypeWrapper.wrap(boundArrayListType).asType(boundCollectionType);
		assertEquals(boundCollectionType, boundArrayListToBoundCollectionType);
		
		Type unboundCollectionToUnboundArrayListType = TypeWrapper.wrap(unboundCollectionType).asType(unboundArrayListType);
		assertEquals(unboundArrayListType, unboundCollectionToUnboundArrayListType);
		
		Type unboundArrayListToUnboundCollectionType = TypeWrapper.wrap(unboundArrayListType).asType(unboundCollectionType);
		assertEquals(unboundCollectionType, unboundArrayListToUnboundCollectionType);
		
		
		
		Type boundCollectionToUnboundArrayListType = TypeWrapper.wrap(boundCollectionType).asType(unboundArrayListType);
		assertEquals(boundArrayListType, boundCollectionToUnboundArrayListType);
		
		Type unboundArrayListToBoundCollectionType = TypeWrapper.wrap(unboundArrayListType).asType(boundCollectionType);
		assertEquals(boundCollectionType, unboundArrayListToBoundCollectionType);
		
		Type unboundCollectionToBoundArrayListType = TypeWrapper.wrap(unboundCollectionType).asType(boundArrayListType);
		assertEquals(boundArrayListType, unboundCollectionToBoundArrayListType);
		
		Type boundArrayListToUnboundCollectionType = TypeWrapper.wrap(boundArrayListType).asType(unboundCollectionType);
		assertEquals(boundCollectionType, boundArrayListToUnboundCollectionType);
		
		
		
		Type boundCollectionToUnboundCollectionType = TypeWrapper.wrap(boundCollectionType).asType(unboundCollectionType);
		assertEquals(boundCollectionType, boundCollectionToUnboundCollectionType);
		
		Type unboundCollectionToBoundCollectionType = TypeWrapper.wrap(unboundCollectionType).asType(boundCollectionType);
		assertEquals(boundCollectionType, unboundCollectionToBoundCollectionType);
		
		Type boundCollectionToBoundCollectionType = TypeWrapper.wrap(boundCollectionType).asType(boundCollectionType);
		assertEquals(boundCollectionType, boundCollectionToBoundCollectionType);
		
		Type unboundCollectionToUnboundCollectionType = TypeWrapper.wrap(unboundCollectionType).asType(unboundCollectionType);
		assertEquals(unboundCollectionType, unboundCollectionToUnboundCollectionType);
		
		
		
		Type boundArrayListToUnboundArrayListType = TypeWrapper.wrap(boundArrayListType).asType(unboundArrayListType);
		assertEquals(boundArrayListType, boundArrayListToUnboundArrayListType);
		
		Type unboundArrayListToBoundArrayListType = TypeWrapper.wrap(unboundArrayListType).asType(boundArrayListType);
		assertEquals(boundArrayListType, unboundArrayListToBoundArrayListType);
		
		Type boundArrayListToBoundArrayListType = TypeWrapper.wrap(boundArrayListType).asType(boundArrayListType);
		assertEquals(boundArrayListType, boundArrayListToBoundArrayListType);
		
		Type unboundArrayListToUnboundArrayListType = TypeWrapper.wrap(unboundArrayListType).asType(unboundArrayListType);
		assertEquals(unboundArrayListType, unboundArrayListToUnboundArrayListType);
		
	
		
		Type boundCollectionToObjectType = TypeWrapper.wrap(boundCollectionType).asType(Object.class);
		assertEquals(Object.class, boundCollectionToObjectType);
		
		Type objectToBoundCollectionType = TypeWrapper.wrap(Object.class).asType(boundCollectionType);
		assertEquals(boundCollectionType, objectToBoundCollectionType);
		
		Type unboundCollectionToObjectType = TypeWrapper.wrap(unboundCollectionType).asType(Object.class);
		assertEquals(Object.class, unboundCollectionToObjectType);
		
		Type objectToUnboundCollectionType = TypeWrapper.wrap(Object.class).asType(unboundCollectionType);
		assertEquals(unboundCollectionType, objectToUnboundCollectionType);
		
		
		
		Type boundArrayListToObjectType = TypeWrapper.wrap(boundArrayListType).asType(Object.class);
		assertEquals(Object.class, boundArrayListToObjectType);
		
		Type objectToBoundArrayListType = TypeWrapper.wrap(Object.class).asType(boundArrayListType);
		assertEquals(boundArrayListType, objectToBoundArrayListType);
		
		Type unboundArrayListToObjectType = TypeWrapper.wrap(unboundArrayListType).asType(Object.class);
		assertEquals(Object.class, unboundArrayListToObjectType);
		
		Type objectToUnboundArrayListType = TypeWrapper.wrap(Object.class).asType(unboundArrayListType);
		assertEquals(unboundArrayListType, objectToUnboundArrayListType);
		
		
		Assert.assertEquals(Object.class, TypeWrapper.wrap(Object.class).asType(Object.class));
	}

	@Test
	public void testAsTypeArray() {
		Type boundCollectionArrayType = new TypeToken<Collection<String>[]>(){}.getType();
		Type unboundCollectionArrayType = new TypeToken<Collection[]>(){}.getType();
		Type boundArrayListArrayType = new TypeToken<ArrayList<String>[]>(){}.getType();
		Type unboundArrayListArrayType = new TypeToken<ArrayList[]>(){}.getType();
		
		Type boundCollectionArrayToBoundCollectionArrayType = TypeWrapper.wrap(boundCollectionArrayType).asType(boundCollectionArrayType);
		assertEquals(boundCollectionArrayType, boundCollectionArrayToBoundCollectionArrayType);
		
		Type boundCollectionArrayToUnboundCollectionArrayType = TypeWrapper.wrap(boundCollectionArrayType).asType(unboundCollectionArrayType);
		assertEquals(boundCollectionArrayType, boundCollectionArrayToUnboundCollectionArrayType);
		
		Type boundCollectionArrayToBoundArrayListArrayType = TypeWrapper.wrap(boundCollectionArrayType).asType(boundArrayListArrayType);
		assertEquals(boundArrayListArrayType, boundCollectionArrayToBoundArrayListArrayType);
		
		Type boundCollectionArrayToUnboundArrayListArrayType = TypeWrapper.wrap(boundCollectionArrayType).asType(unboundArrayListArrayType);
		assertEquals(boundArrayListArrayType, boundCollectionArrayToUnboundArrayListArrayType);
	}
	
	@Test
	public void testGetRawClass() {
		Type boundCollectionType = new TypeToken<Collection<String>>(){}.getType();
		Type unboundCollectionType = new TypeToken<Collection>(){}.getType();
		Type boundCollectionArrayType = new TypeToken<Collection<String>[]>(){}.getType();
		Type unboundCollectionArrayType = new TypeToken<Collection[]>(){}.getType();
		
		Assert.assertEquals(TypeWrapper.wrap(boundCollectionType).getRawClass(), TypeWrapper.wrap(unboundCollectionType).getRawClass());
		Assert.assertEquals(TypeWrapper.wrap(boundCollectionArrayType).getRawClass(), TypeWrapper.wrap(unboundCollectionArrayType).getRawClass());
	}
	
	@Test
	public void testIsNotAssignable() {
		Type parameterizedCollectionType1 = new TypeToken<Collection<String>>(){}.getType();
		Type parameterizedCollectionType2 = new TypeToken<Collection<Number>>(){}.getType();
		
		assertFalse(TypeWrapper.wrap(parameterizedCollectionType1).isAssignableFrom(parameterizedCollectionType2));
		assertFalse(TypeWrapper.wrap(parameterizedCollectionType1).isWeakAssignableFrom(parameterizedCollectionType2));
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
		
		assertFalse(TypeWrapper.wrap(parameterizedCollectionType).isAssignableFrom(rawCollectionType));
		assertTrue(TypeWrapper.wrap(parameterizedCollectionType).isWeakAssignableFrom(rawCollectionType));
		
		assertTrue(TypeWrapper.wrap(rawCollectionType).isAssignableFrom(parameterizedCollectionType));
		assertTrue(TypeWrapper.wrap(rawCollectionType).isWeakAssignableFrom(parameterizedCollectionType));
		
		
		assertFalse(TypeWrapper.wrap(parameterizedCollectionArrayType).isAssignableFrom(rawCollectionArrayType));
		assertTrue(TypeWrapper.wrap(parameterizedCollectionArrayType).isWeakAssignableFrom(rawCollectionArrayType));
		
		assertTrue(TypeWrapper.wrap(rawCollectionArrayType).isAssignableFrom(parameterizedCollectionArrayType));
		assertTrue(TypeWrapper.wrap(rawCollectionArrayType).isWeakAssignableFrom(parameterizedCollectionArrayType));
		
		
		assertTrue(TypeWrapper.wrap(Object.class).isAssignableFrom(rawCollectionType));
		assertTrue(TypeWrapper.wrap(Object.class).isWeakAssignableFrom(rawCollectionType));
		
		assertTrue(TypeWrapper.wrap(Object.class).isAssignableFrom(parameterizedCollectionType));
		assertTrue(TypeWrapper.wrap(Object.class).isWeakAssignableFrom(parameterizedCollectionType));
		
		assertTrue(TypeWrapper.wrap(Object.class).isAssignableFrom(rawCollectionArrayType));
		assertTrue(TypeWrapper.wrap(Object.class).isWeakAssignableFrom(rawCollectionArrayType));
		
		assertTrue(TypeWrapper.wrap(Object.class).isAssignableFrom(parameterizedCollectionArrayType));
		assertTrue(TypeWrapper.wrap(Object.class).isWeakAssignableFrom(parameterizedCollectionArrayType));
		
		
		assertTrue(TypeWrapper.wrap(objectArrayClass).isAssignableFrom(rawCollectionArrayType));
		assertTrue(TypeWrapper.wrap(objectArrayClass).isWeakAssignableFrom(rawCollectionArrayType));
		
		assertTrue(TypeWrapper.wrap(objectArrayClass).isAssignableFrom(parameterizedCollectionArrayType));
		assertTrue(TypeWrapper.wrap(objectArrayClass).isWeakAssignableFrom(parameterizedCollectionArrayType));
		
		assertTrue(TypeWrapper.wrap(objectArrayClass).isAssignableFrom(rawCollectionArray2Type));
		assertTrue(TypeWrapper.wrap(objectArrayClass).isWeakAssignableFrom(rawCollectionArray2Type));
		
		assertTrue(TypeWrapper.wrap(objectArrayClass).isAssignableFrom(parameterizedCollectionArray2Type));
		assertTrue(TypeWrapper.wrap(objectArrayClass).isWeakAssignableFrom(parameterizedCollectionArray2Type));
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
		
		assertFalse(TypeWrapper.wrap(parameterizedCollectionType).isAssignableFrom(wildCardCollectionType));
		assertTrue(TypeWrapper.wrap(parameterizedCollectionType).isWeakAssignableFrom(wildCardCollectionType));
		
		assertTrue(TypeWrapper.wrap(wildCardCollectionType).isAssignableFrom(parameterizedCollectionType));
		assertTrue(TypeWrapper.wrap(wildCardCollectionType).isWeakAssignableFrom(parameterizedCollectionType));
		
		
		assertFalse(TypeWrapper.wrap(parameterizedCollectionArrayType).isAssignableFrom(wildCardCollectionArrayType));
		assertTrue(TypeWrapper.wrap(parameterizedCollectionArrayType).isWeakAssignableFrom(wildCardCollectionArrayType));
		
		assertTrue(TypeWrapper.wrap(wildCardCollectionArrayType).isAssignableFrom(parameterizedCollectionArrayType));
		assertTrue(TypeWrapper.wrap(wildCardCollectionArrayType).isWeakAssignableFrom(parameterizedCollectionArrayType));
		
		
		assertTrue(TypeWrapper.wrap(Object.class).isAssignableFrom(wildCardCollectionType));
		assertTrue(TypeWrapper.wrap(Object.class).isWeakAssignableFrom(wildCardCollectionType));
		
		assertTrue(TypeWrapper.wrap(Object.class).isAssignableFrom(parameterizedCollectionType));
		assertTrue(TypeWrapper.wrap(Object.class).isWeakAssignableFrom(parameterizedCollectionType));
		
		assertTrue(TypeWrapper.wrap(Object.class).isAssignableFrom(wildCardCollectionArrayType));
		assertTrue(TypeWrapper.wrap(Object.class).isWeakAssignableFrom(wildCardCollectionArrayType));
		
		assertTrue(TypeWrapper.wrap(Object.class).isAssignableFrom(parameterizedCollectionArrayType));
		assertTrue(TypeWrapper.wrap(Object.class).isWeakAssignableFrom(parameterizedCollectionArrayType));
		
		
		assertTrue(TypeWrapper.wrap(objectArrayClass).isAssignableFrom(wildCardCollectionArrayType));
		assertTrue(TypeWrapper.wrap(objectArrayClass).isWeakAssignableFrom(wildCardCollectionArrayType));
		
		assertTrue(TypeWrapper.wrap(objectArrayClass).isAssignableFrom(parameterizedCollectionArrayType));
		assertTrue(TypeWrapper.wrap(objectArrayClass).isWeakAssignableFrom(parameterizedCollectionArrayType));
		
		assertTrue(TypeWrapper.wrap(objectArrayClass).isAssignableFrom(wildCardCollectionArray2Type));
		assertTrue(TypeWrapper.wrap(objectArrayClass).isWeakAssignableFrom(wildCardCollectionArray2Type));
		
		assertTrue(TypeWrapper.wrap(objectArrayClass).isAssignableFrom(parameterizedCollectionArray2Type));
		assertTrue(TypeWrapper.wrap(objectArrayClass).isWeakAssignableFrom(parameterizedCollectionArray2Type));
	}
	
	@Test
	public void testIsAssignableWithWildCardType2() {
		Type wildCardCollectionType1 = new TypeToken<Collection<?>>(){}.getType();
		Type wildCardCollectionType2 = new TypeToken<Collection<?>>(){}.getType();
		
		assertTrue(TypeWrapper.wrap(wildCardCollectionType1).isAssignableFrom(wildCardCollectionType2));
		assertTrue(TypeWrapper.wrap(wildCardCollectionType1).isWeakAssignableFrom(wildCardCollectionType2));
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
