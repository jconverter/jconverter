package org.jconverter;

import org.jconverter.instantiation.InstanceCreatorTest;
import org.jconverter.typesolver.TypeSolverTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({InstanceCreatorTest.class, TypeSolverTest.class})
public class JConverterTestSuite {
}
