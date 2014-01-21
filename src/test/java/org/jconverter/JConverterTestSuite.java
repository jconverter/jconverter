package org.jconverter;

import org.jconverter.converter.ConverterTest;
import org.jconverter.factory.FactoryTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({FactoryTest.class, ConverterTest.class, JConverterTutorialTest.class})
public class JConverterTestSuite {
}
