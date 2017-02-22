package org.jconverter.converter;


public class ConversionError extends AbstractConversionException {

    private static final long serialVersionUID = 1L;

    public ConversionError(ConversionGoal conversionGoal) {
        super(conversionGoal);
    }

    public ConversionError(ConversionGoal conversionGoal, Throwable cause) {
        super(conversionGoal, cause);
    }

    @Override
    public String getMessage() {
        return "Cannot convert from " + getConversionGoal().getSource() + " to " + getConversionGoal() + ".";
    }

}
