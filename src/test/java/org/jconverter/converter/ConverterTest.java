package org.jconverter.converter;

import org.jconverter.converter.catalog.NumberConverterTest;
import org.jconverter.converter.catalog.StringConverterTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({StringConverterTest.class, NumberConverterTest.class})
public class ConverterTest {
}
