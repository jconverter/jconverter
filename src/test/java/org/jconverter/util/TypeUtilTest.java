package org.jconverter.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;
import org.jconverter.util.FixtureTypeUtil.Class6;
import org.jconverter.util.FixtureTypeUtil.Class7;
import org.jconverter.util.FixtureTypeUtil.MyMap;
import org.jconverter.util.FixtureTypeUtil.MyMap3;
import org.jconverter.util.FixtureTypeUtil.MyMap4;
import org.jconverter.util.FixtureTypeUtil.MyMap5;
import org.jconverter.util.FixtureTypeUtil.MyMap6;
import org.jconverter.util.typewrapper.TypeWrapper;

import com.google.common.reflect.TypeToken;

/**
 * 
 * @author scastro
 *
 */
public class TypeUtilTest {

	@Test
	public void testBindingType() {
		Type unboundCollectionAncestorType = Collection.class;
		Type boundCollectionAncestorType = new TypeToken<Collection<Integer>>(){}.getType();
		Type boundCollectionDescendantType = new TypeToken<ArrayList<String>>(){}.getType();
		Type t = null;
		
		t = TypeUtil.bindTypeGivenAncestor(Object.class, String.class);
		assertEquals(String.class, t);
		
		t = TypeUtil.bindTypeGivenDescendant(Object.class, String.class);
		assertEquals(Object.class, t);
		
		
		Type boundEntryType = new TypeToken<Entry<String,String>>(){}.getType();
		
		t = TypeUtil.bindTypeGivenAncestor(Object.class, boundEntryType);
		assertEquals(boundEntryType, t);
		
		t = TypeUtil.bindTypeGivenDescendant(Object.class, boundEntryType);
		assertEquals(Object.class, t);
		
		
		t = TypeUtil.bindTypeGivenDescendant(unboundCollectionAncestorType, boundCollectionDescendantType);
		assertEquals(new TypeToken<Collection<String>>(){}.getType(), t);
		
		t = TypeUtil.bindTypeGivenDescendant(Object.class, boundCollectionDescendantType);
		assertEquals(Object.class, t);
		
		try {
			t = TypeUtil.bindTypeGivenDescendant(boundCollectionAncestorType, boundCollectionDescendantType);
			fail();
		} catch(Exception e){}
		
		t = TypeUtil.bindTypeGivenAncestor(unboundCollectionAncestorType, boundCollectionDescendantType);
		assertEquals(new TypeToken<ArrayList<String>>(){}.getType(), t);
		t = TypeUtil.bindTypeGivenAncestor(Object.class, boundCollectionDescendantType);
		assertEquals(new TypeToken<ArrayList<String>>(){}.getType(), t);
		
		try {
			t = TypeUtil.bindTypeGivenAncestor(boundCollectionAncestorType, boundCollectionDescendantType);
			fail();
		} catch(Exception e){}

		t = TypeUtil.bindTypeGivenDescendant(Object.class, FixtureReflectionTests.unboundTypeVariable);

		t = TypeUtil.bindTypeGivenDescendant(Object.class, new Object[]{}.getClass());
		assertEquals(Object.class, t);
		
		t = TypeUtil.bindTypeGivenDescendant(new Object[]{}.getClass(), new Object[]{}.getClass());
		assertEquals(new Object[]{}.getClass(), t);
		
		t = TypeUtil.bindTypeGivenDescendant(new Object[]{}.getClass(), new Object[][]{}.getClass());
		assertEquals(new Object[]{}.getClass(), t);
	}
	
	@Test
	public void testUnifying() {
		Type ancestorType = MyMap3.class.getGenericSuperclass(); //MyMap2<java.util.List<X>, X>
		Type descendantType = MyMap4.class; //MyMap4

		Map<TypeVariable, Type> typeVars = TypeUtil.unifyWithDescendant(ancestorType, descendantType);
		
		TypeVariable typeVar = (TypeVariable) typeVars.keySet().toArray()[0];
		assertEquals(typeVar.getName(), "X");
		Type t = typeVars.get(typeVar);
		assertEquals(t, String.class);
	}
	
	@Test
	public void testUnifyingWildCard() {
		Type typeListString = new TypeToken<List<String>>(){}.getType();
		Type typeListWildcard = new TypeToken<List<?>>(){}.getType();
		Type typeArrayListString = new TypeToken<ArrayList<String>>(){}.getType();
		Type typeArrayListWildcard = new TypeToken<ArrayList<?>>(){}.getType();
		
		assertEquals(typeListString, TypeUtil.bindTypeGivenAncestor(typeListString, typeListWildcard));
		assertEquals(typeListString, TypeUtil.bindTypeGivenAncestor(typeListWildcard, typeListString));
		assertEquals(typeListWildcard, TypeUtil.bindTypeGivenAncestor(typeListWildcard, typeListWildcard));
		assertEquals(typeListString, TypeUtil.bindTypeGivenDescendant(typeListString, typeListWildcard));
		assertEquals(typeListString, TypeUtil.bindTypeGivenDescendant(typeListWildcard, typeListString));
		assertEquals(typeListWildcard, TypeUtil.bindTypeGivenDescendant(typeListWildcard, typeListWildcard));
		
		assertEquals(typeArrayListString, TypeUtil.bindTypeGivenAncestor(typeListString, typeArrayListString));
		assertEquals(typeArrayListString, TypeUtil.bindTypeGivenAncestor(typeListString, typeArrayListWildcard));
		assertEquals(typeListString, TypeUtil.bindTypeGivenDescendant(typeListString, typeArrayListString));
		assertEquals(typeListString, TypeUtil.bindTypeGivenDescendant(typeListString, typeArrayListWildcard));
		
		assertEquals(typeArrayListString, TypeUtil.bindTypeGivenAncestor(typeListWildcard, typeArrayListString));
		assertEquals(typeArrayListWildcard, TypeUtil.bindTypeGivenAncestor(typeListWildcard, typeArrayListWildcard));
		assertEquals(typeListString, TypeUtil.bindTypeGivenDescendant(typeListWildcard, typeArrayListString));
		assertEquals(typeListWildcard, TypeUtil.bindTypeGivenDescendant(typeListWildcard, typeArrayListWildcard));
	}
	
	@Test
	public void testUnifyingArrayType() {
		Type ancestorType = MyMap5.class.getGenericSuperclass(); //MyMap2<java.util.List<X>, X>
		Type descendantType = MyMap6.class; //MyMap4

		Map<TypeVariable, Type> typeVars = TypeUtil.unifyWithDescendant(ancestorType, descendantType);
		
		TypeVariable typeVar = (TypeVariable) typeVars.keySet().toArray()[0];
		assertEquals(typeVar.getName(), "X");
		Type t = typeVars.get(typeVar);
		assertEquals(t, String.class);
		
//		System.out.println("Ancestor type: " + ancestorType);
//		System.out.println("Descendant type: " + descendantType);
//		System.out.println("Unified type variables" + typeVars);
		
	}
	
	@Test
	public void testFindAncestorTypeParametersMap() {
		Map<TypeVariable, Type> typeArgumentsMap = TypeUtil.findAncestorTypeParametersMap(MyMap.class, Class6.class);
		//System.out.println("Arguments map: " + typeArgumentsMap);
		
		TypeWrapper typeWrapper1 = TypeWrapper.wrap(typeArgumentsMap.get(MyMap.class.getTypeParameters()[0]));
		TypeWrapper typeWrapper2 = TypeWrapper.wrap(typeArgumentsMap.get(MyMap.class.getTypeParameters()[1]));
		
		//System.out.println(typeWrapper1);
		//System.out.println(typeWrapper2);
		
		assertEquals(typeWrapper1.getRawClass(), Map.class);
		assertEquals(typeWrapper2.getRawClass(), List.class);
		
		//System.out.println(typeArgumentsMap.get(MyMap.class.getTypeParameters()[0]));
		//System.out.println(typeArgumentsMap.get(MyMap.class.getTypeParameters()[1]));
	}
	
	
	@Test
	public void testGenericInference() {
		Field f;
		try {
			f = FixtureTypeUtil.class.getField("myField");
		} catch (NoSuchFieldException | SecurityException e1) {
			throw new RuntimeException(e1);
		}

		ParameterizedType pt = (ParameterizedType) f.getGenericType(); //Class4<java.util.Iterator<java.util.Map<?, java.lang.String>>>
		
		Type[] descendantTypes = TypeUtil.findDescendantTypeParameters(pt, Class7.class);
		assertEquals(descendantTypes.length, 2);
		assertEquals(((TypeVariable)descendantTypes[0]).getName(), "X");
		assertEquals(TypeWrapper.wrap(descendantTypes[1]).getRawClass(), Iterator.class);
	}

	
	public static interface i0 {}
	public static interface i1 {}
	public static interface i2 extends i1, i0 {}
	
	
	public static void main(String[] args) {
		System.out.println(i2.class.getSuperclass());
		System.out.println(i2.class.getInterfaces().length);
	}
	
}
