package org.jconverter.converter;


import java.util.function.Function;

public abstract class ConverterEvaluator<T, U> implements Function<Converter<T,U>, U> {

    private final ConversionGoal conversionGoal;

    protected ConverterEvaluator(ConversionGoal conversionGoal) {
        this.conversionGoal = conversionGoal;
    }

    public ConversionGoal getConversionGoal() {
        return conversionGoal;
    }
}
