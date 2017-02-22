package org.jconverter.util;


import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class NonRedundantEvaluator<T, U> implements Function<T, U> {

    private final Function<T, U> evaluator;
    private final Set<T> visited;


    public NonRedundantEvaluator(Function<T, U> evaluator) {
        this.evaluator = evaluator;
        visited = new HashSet<>();
    }

    @Override
    public U apply(T arg) {
        if (visited.contains(arg)) {
            throw new AlreadyEvaluatedException();
        } else {
            visited.add(arg);
            return this.evaluator.apply(arg);
        }
    }

    public static class AlreadyEvaluatedException extends RuntimeException {}

}
