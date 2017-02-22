package org.jconverter.converter

import org.jconverter.JConverter
import org.jconverter.JConverterBuilder
import spock.lang.Specification

import static org.jconverter.converter.ConversionGoal.conversionGoal

class ConverterSpec extends Specification {

    private static class A {}
    private static class B {}

    private static Converter<A, String> aConverter = new Converter<A, String>() {
        @Override
        String apply(A sourceObject, TypeDomain target, JConverter context) {
            context.convert(new B(), String.class)
        }
    }

    private static Converter<B, String> bConverterThrowingRuntimeException = new Converter<B, String>() {
        @Override
        String apply(B sourceObject, TypeDomain target, JConverter context) {
            throw new RuntimeException()
        }
    }

    private static Converter<B, String> bConverterThrowingConversionError = new Converter<B, String>() {
        @Override
        String apply(B sourceObject, TypeDomain target, JConverter context) {
            throw new ConversionError(conversionGoal(sourceObject, target))
        }
    }


    def 'A ConversionError thrown by a converter is re-thrown by the conversion context'() {
        given:
        JConverter jConverter = JConverterBuilder.create()
            .register(bConverterThrowingConversionError)
            .build()

        when:
        jConverter.convert(new B(), String.class)

        then:
        ConversionError conversionException = thrown()
        conversionException.cause == null
    }


    def 'A RuntimeException thrown by a converter is wrapped into a ConversionError'() {
        given:
        JConverter jConverter = JConverterBuilder.create()
                .register(bConverterThrowingRuntimeException)
                .build()

        when:
        jConverter.convert(new B(), String.class)

        then:
        ConversionError conversionException = thrown()
        conversionException.cause != null
        ! (conversionException.cause instanceof ConversionError)
        conversionException instanceof RuntimeException
    }


    def 'A ConversionError thrown by a nested conversion is wrapped into another ConversionError'() {
        given:
        JConverter jConverter = JConverterBuilder.create()
                .register(aConverter)
                .register(bConverterThrowingRuntimeException)
                .build()

        when:
        jConverter.convert(new A(), String.class)

        then:
        ConversionError conversionException = thrown()
        conversionException.cause != null
        conversionException.cause instanceof ConversionError
        conversionException.cause.cause != null
        ! (conversionException.cause.cause instanceof ConversionError)
        conversionException.cause.cause instanceof RuntimeException
    }

}
