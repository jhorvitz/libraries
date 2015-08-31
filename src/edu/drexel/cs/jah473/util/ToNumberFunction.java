package edu.drexel.cs.jah473.util;

import java.util.function.Function;

/**
 * Represents a function that produces a numeric result.
 * 
 * @author Justin Horvitz
 *
 * @param <T>
 *            the type of the input to the function
 * @param <R>
 *            the type of the output of the function, must be numeric
 */
@FunctionalInterface
public interface ToNumberFunction<T, R extends Number> extends Function<T, R> {

}