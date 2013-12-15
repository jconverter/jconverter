package org.jconverter.converter;

import java.lang.reflect.Type;


/**
 * This exception is thrown when a problem occurs when converting an arbitrary object.
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
	
	public ConversionException(String from, String to) {
		super(formatMessage(from, to));
	}
	
	public ConversionException(String from, String to, String reason) {
		super(formatMessage(from, to, reason));
	}
	
	public ConversionException(String from, String to, Exception ex) {
		super(formatMessage(from, to), ex);
	}
	
	
	private static String formatMessage(String from, String to) {
		return formatMessage(from, to, null);
	}
	
	private static String formatMessage(String from, String to, String reason) {
		StringBuilder sb = new StringBuilder("Impossible to convert from " + from + " to " + to + ". ");
		if(reason != null)
			sb.append("Cause: " + reason);
		return sb.toString();
	}

}
