package org.jconverter.converter.catalog.string;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.jconverter.JConverter;
import org.jconverter.converter.Converter;
import org.jconverter.converter.TypeDomain;

public class StringToDateConverter implements Converter<String, Date> {

	@Override
	public Date apply(String source, TypeDomain target, JConverter context) {
		DateFormat df = context.instantiate(DateFormat.class);
		try {
			return df.parse(source);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}
