package org.jconverter.converter.catalog.string;

import static org.jconverter.converter.TypeDomain.typeDomain;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;

public class StringToXMLGregorianCalendarConverter implements Converter<String, XMLGregorianCalendar> {

	@Override
	public XMLGregorianCalendar apply(String source, TypeDomain target, JConverter context) {
		GregorianCalendar gregCal = new StringToGregorianCalendarConverter().apply(source, typeDomain(GregorianCalendar.class), context);
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

}
