package org.jconverter.converter;

import org.jconverter.converter.catalog.array.ObjectArrayConverterTest;
import org.jconverter.converter.catalog.array.PrimitiveArrayConverterTest;
import org.jconverter.converter.catalog.iterator.IteratorConverterTest;
import org.jconverter.converter.catalog.map.MapConverterTest;
import org.jconverter.converter.catalog.number.NumberConverterTest;
import org.jconverter.converter.catalog.string.StringConverterTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({StringConverterTest.class, NumberConverterTest.class, IteratorConverterTest.class, ObjectArrayConverterTest.class, PrimitiveArrayConverterTest.class, MapConverterTest.class})
public class ConverterTest {
}
