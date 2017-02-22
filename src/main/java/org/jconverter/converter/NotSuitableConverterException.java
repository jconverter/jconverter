package org.jconverter.converter;

/**
 * This exception is thrown when no registered converter can accomplish a conversion.
 *
 */
public class NotSuitableConverterException extends AbstractConversionException {

    private static final long serialVersionUID = 1L;

    public NotSuitableConverterException(ConversionGoal conversionGoal) {
        super(conversionGoal);
    }

    public NotSuitableConverterException(ConversionGoal conversionGoal, Throwable cause) {
        super(conversionGoal, cause);
    }

    @Override
    public String getMessage() {
        return "No suitable converter can convert from " + getConversionGoal().getSource() + " to " + getConversionGoal().getTarget() + ".";
    }

}
