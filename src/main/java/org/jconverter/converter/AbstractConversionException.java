package org.jconverter.converter;


public abstract class AbstractConversionException extends RuntimeException {

    private final ConversionGoal conversionGoal;

    public AbstractConversionException(ConversionGoal conversionGoal) {
        this.conversionGoal = conversionGoal;
    }

    public AbstractConversionException(ConversionGoal conversionGoal, Throwable cause) {
        super(cause);
        this.conversionGoal = conversionGoal;
    }

    public ConversionGoal getConversionGoal() {
        return conversionGoal;
    }
}
