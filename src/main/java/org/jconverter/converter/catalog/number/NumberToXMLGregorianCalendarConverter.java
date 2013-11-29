package org.jconverter.converter.catalog.number;

import java.lang.reflect.Type;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;

public class NumberToXMLGregorianCalendarConverter implements Converter<Number, XMLGregorianCalendar> {

	@Override
	public XMLGregorianCalendar apply(Number source, Type targetType, JConverter context) {
		GregorianCalendar gregCal = new NumberToGregorianCalendarConverter().apply(source, GregorianCalendar.class, context);
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

}
