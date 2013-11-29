package org.jconverter.converter.catalog.string;

import java.lang.reflect.Type;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter.DefaultConverter;

public class StringToXMLGregorianCalendarConverter extends DefaultConverter<String, XMLGregorianCalendar> {

	@Override
	public XMLGregorianCalendar apply(String source, Type targetType, JConverter context) {
		GregorianCalendar gregCal = new StringToGregorianCalendarConverter().apply(source, GregorianCalendar.class, context);
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

}
