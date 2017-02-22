package org.jconverter.converter;

/**
 * This exception is thrown when a converter delegates the conversion to the next best available converter.
 *
 */
public class DelegateConversionException extends AbstractConversionException {

	private static final long serialVersionUID = 1L;

	public DelegateConversionException(ConversionGoal conversionGoal) {
		super(conversionGoal);
	}
	
	public DelegateConversionException(ConversionGoal conversionGoal, Throwable cause) {
		super(conversionGoal, cause);
	}

	@Override
	public String getMessage() {
		return "Delegating conversion of " + getConversionGoal().getSource() + " to " + getConversionGoal().getTarget() + ".";
	}

}
