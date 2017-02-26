package org.jconverter.converter;

import static org.jconverter.converter.ConversionGoal.conversionGoal;
import static org.jconverter.converter.TypeDomain.typeDomain;

import java.lang.reflect.Type;
import java.util.Objects;

import org.jconverter.JConverter;
import org.typeutils.typewrapper.TypeWrapper;

/**
 * A conversion function that defines and domain, range and operation.
 * @param <T> the source type of the operation.
 * @param <U> the target type of the operation.
 */
public class ConversionFunction<T,U> implements Converter<T,U> {

	private static ConversionDomains getConversionDomains(Converter<?,?> converter) {
		Type sourceType;
		Type targetType;
		TypeWrapper converterTypeWrapper = TypeWrapper.wrap(converter.getClass()).as(Converter.class);
		if (converterTypeWrapper.hasActualTypeArguments()) {
			sourceType = converterTypeWrapper.getActualTypeArguments()[0];
			targetType = converterTypeWrapper.getActualTypeArguments()[1];
			return ConversionDomains.conversionTypes(typeDomain(sourceType), typeDomain(targetType));
		} else {
			/*logger.warn("Converter does not specify parameter types. Source and target types will be assumed the Object class.");
			sourceType = Object.class;
			returnType = Object.class;
			return conversionTypes(sourceType, targetType);*/
			throw new BadConverterException(converter, "Converter does not specify parameter types.");
		}
	}

	public static <T,U> ConversionFunction<T,U> forConverter(Converter<T,U> converter) {
		if (converter instanceof ConversionFunction) {
			return (ConversionFunction) converter;
		} else {
			return new ConversionFunction<>(converter);
		}
	}

	public static <T,U> ConversionFunction<T,U> forConverter(Converter<T,U> converter, TypeDomain sourceDomain, TypeDomain targetDomain) {
		return new ConversionFunction<>(converter, sourceDomain, targetDomain);
	}


	private final TypeDomain domain;
	private final TypeDomain range;
	private final Converter<T,U> operation;

	private ConversionFunction(Converter<T,U> converter) {
		this(converter, getConversionDomains(converter));
	}

	private ConversionFunction(Converter<T,U> converter, ConversionDomains conversionTypes) {
		this(converter, conversionTypes.getSource(), conversionTypes.getTarget());
	}

	private ConversionFunction(Converter<T,U> converter, TypeDomain domain, TypeDomain range) {
		this.operation = converter;
		this.domain = domain;
		this.range = range;
	}

	public Converter<T,U> getOperation() {
		return operation;
	}

	public TypeDomain getDomain() {
		return domain;
	}

	public TypeDomain getRange() {
		return range;
	}

	@Override
	public U apply(T source, TypeDomain target, JConverter context) {
		if (!domain.contains(source) || !target.isSubsetOf(range)) {
			throw new DelegateConversionException(conversionGoal(source, target));
		}
		return operation.apply(source, target, context);
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final ConversionFunction<?, ?> that = (ConversionFunction<?, ?>) o;
		return Objects.equals(domain, that.domain) &&
				Objects.equals(range, that.range) &&
				Objects.equals(operation, that.operation);
	}

	@Override
	public int hashCode() {
		return Objects.hash(domain, range, operation);
	}





	private static class ConversionDomains {

		private final TypeDomain source;
		private final TypeDomain target;

		private ConversionDomains(TypeDomain source, TypeDomain target) {
			this.source = source;
			this.target = target;
		}

		public static ConversionDomains conversionTypes(TypeDomain source, TypeDomain target) {
			return new ConversionDomains(source, target);
		}

		public TypeDomain getSource() {
			return source;
		}

		public TypeDomain getTarget() {
			return target;
		}
	}

}
