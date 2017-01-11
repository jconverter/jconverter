package org.jconverter.internal.reflection;

import org.junit.Assert;
import org.junit.Test;
import org.jconverter.internal.reflection.FixtureReflectionUtil.Abstract;
import org.jconverter.internal.reflection.FixtureReflectionUtil.Concrete;
import org.jconverter.internal.reflection.FixtureReflectionUtil.Interface;

public class ReflectionUtilTest {

	@Test
	public void testIsInterface() {
		Assert.assertTrue(ReflectionUtil.isInterface(Interface.class));
		Assert.assertFalse(ReflectionUtil.isInterface(Abstract.class));
		Assert.assertFalse(ReflectionUtil.isInterface(Concrete.class));
	}

	@Test
	public void testIsAbstract() {
		Assert.assertTrue(ReflectionUtil.isAbstract(Interface.class));
		Assert.assertTrue(ReflectionUtil.isAbstract(Abstract.class));
		Assert.assertTrue(ReflectionUtil.isAbstract(int.class)); //primitive classes are considered abstract
		Assert.assertFalse(ReflectionUtil.isAbstract(Concrete.class));
	}
	
}
