package org.jconverter.converter;

import java.lang.reflect.Type;


/**
 * This exception is thrown when a problem occurs when converting between objects.
 * @author sergioc
 *
 */
public class ConversionException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ConversionException() {
	}
	
	public ConversionException(Type from, Type to) {
		this(from.toString(), to.toString());
	}
	
	public ConversionException(Type from, Type to, Exception cause) {
		this(from.toString(), to.toString(), cause);
	}
	
	public ConversionException(String from, String to) {
		super(formatMessage(from, to));
	}
	
	public ConversionException(String from, String to, String cause) {
		super(formatMessage(from, to, cause));
	}
	
	public ConversionException(String from, String to, Exception cause) {
		super(formatMessage(from, to), cause);
	}
	
	
	private static String formatMessage(String from, String to) {
		return formatMessage(from, to, null);
	}
	
	private static String formatMessage(String from, String to, String cause) {
		StringBuilder sb = new StringBuilder("Impossible to convert from " + from + " to " + to + ". ");
		if(cause != null)
			sb.append("Cause: " + cause);
		return sb.toString();
	}

}
