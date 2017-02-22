package org.jconverter.factory;

public class FactoryException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FactoryException() {
	}

	public FactoryException(Throwable cause) {
		super(cause);
	}

/*	public FactoryException(Type type) {
		super(formatMessage(type));
	}

	public FactoryException(Type type, Throwable cause) {
		super(formatMessage(type), cause);
	}*/

/*	private static String formatMessage(Type type) {
		return "Impossible to instantiate type " + type + ".";
	}*/

}
