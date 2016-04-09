package pl.ciruk.blog.fanout;

import java.util.Arrays;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

public class CompletableFutures {
	static <F, T> Function<F, CompletableFuture<Stream<T>>> fanOutAsync(Function<F, T>... mappings) {
		Function<F, CompletableFuture<Stream<T>>> reduce = input -> Arrays.stream(mappings)
				.map(mapper -> mapper.apply(input)) // from F to T
				.map(Stream::of) // from T to Stream<T>
				.map(CompletableFutures::of) // from Stream<T> to CompletableFuture<Stream<T>>
				.reduce(CompletableFuture.completedFuture(Stream.empty()), combineUsing(Stream::concat));
		return reduce;
	}

	static <F, T> Function<F, Stream<T>> fanOut(Function<F, T>... mappings) {
		Function<F, Stream<T>> reduce = input -> Arrays.stream(mappings)
				.map(mapper -> mapper.apply(input)) // from F to T
				.map(Stream::of) // from T to Stream<T>
				.reduce(Stream.empty(), Stream::concat);
		return reduce;
	}

	public static <T> BinaryOperator<CompletableFuture<T>> combineUsing(BiFunction<T, T, T> combiningFunction) {
		return (cf1, cf2) -> cf1.thenCombineAsync(cf2, combiningFunction);
	}

	public static <T> CompletableFuture<T> of(T t) {
		return CompletableFuture.supplyAsync(() -> t);
	}

	private CompletableFutures() {
		// Utility class
	}
}
