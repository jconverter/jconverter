package org.jconverter.converter.catalog.number;

import static org.jconverter.converter.TypeDomain.typeDomain;

import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;

public class NumberToXMLGregorianCalendarConverter implements Converter<Number, XMLGregorianCalendar> {

	@Override
	public XMLGregorianCalendar apply(Number source, TypeDomain target, JConverter context) {
		GregorianCalendar gregCal = new NumberToGregorianCalendarConverter().apply(source, typeDomain(GregorianCalendar.class), context);
		try {
			return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
		} catch (DatatypeConfigurationException e) {
			throw new RuntimeException(e);
		}
	}

}
