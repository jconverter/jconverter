package org.jconverter.converter;


import java.util.List;
import java.util.function.Function;

import org.jgum.ChainOfResponsibilityExhaustedException;
import org.jgum.strategy.ChainOfResponsibility;

public class ConverterChain<T,U> extends ChainOfResponsibility<T,U> {

    protected final ConversionGoal conversionGoal;

    public ConverterChain(ConversionGoal conversionGoal, List<T> responsibilityChain) {
        super(responsibilityChain, DelegateConversionException.class);
        this.conversionGoal = conversionGoal;
    }

    @Override
    public U apply(Function<T, U> evaluator) {
        try {
            return super.apply(evaluator);
        } catch (ChainOfResponsibilityExhaustedException e) {
            return onChainExhausted(e);
        }
    }

    protected U onChainExhausted(ChainOfResponsibilityExhaustedException cause) {
        throw new DelegateConversionException(conversionGoal, cause);
    }


/*

    public static class IntermediateChain<T,U> extends ConverterChain<T,U> {

        public IntermediateChain(Object sourceObject, Type targetType, List<T> responsibilityChain) {
            super(conversionGoal, responsibilityChain, CannotConvertException.class);
        }

        @Override
        protected U onChainExhausted(ChainOfResponsibilityExhaustedException cause) {
            throw new CannotConvertException(conversionGoal, cause);
        }
    }
*/

    public static class TopLevelChain<T,U> extends ConverterChain<T,U> {

        public TopLevelChain(ConversionGoal conversionGoal, List<T> responsibilityChain) {
            super(conversionGoal, responsibilityChain);
        }

        @Override
        protected U onChainExhausted(ChainOfResponsibilityExhaustedException cause) {
            throw new NotSuitableConverterException(conversionGoal, cause);
        }
    }

}
