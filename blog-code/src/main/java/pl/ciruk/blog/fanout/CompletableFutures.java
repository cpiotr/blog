package pl.ciruk.blog.fanout;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Stream;

public class CompletableFutures {
	static <F, T> Function<F, CompletableFuture<Stream<T>>> fanOut(Collection<Function<F, T>> mappings) {
		return fanOut(mappings, ForkJoinPool.commonPool());
	}

	static <F, T> Function<F, CompletableFuture<Stream<T>>> fanOut(Collection<Function<F, T>> mappings, ExecutorService executorService) {
		Function<F, CompletableFuture<Stream<T>>> reduce = input -> mappings.stream()
				.map(mapper -> mapper.apply(input)) // from F to T
				.map(Stream::of) // from T to Stream<T>
				.map(CompletableFutures::of) // from Stream<T> to CompletableFuture<Stream<T>>
				.reduce(CompletableFuture.completedFuture(Stream.empty()), combineUsing(Stream::concat, executorService));
		return reduce;
	}

	public static <T> BinaryOperator<CompletableFuture<T>> combineUsing(BiFunction<T, T, T> combiningFunction) {
		return (cf1, cf2) -> cf1.thenCombineAsync(cf2, combiningFunction);
	}

	public static <T> BinaryOperator<CompletableFuture<T>> combineUsing(BiFunction<T, T, T> combiningFunction, ExecutorService executorService) {
		return (cf1, cf2) -> cf1.thenCombineAsync(cf2, combiningFunction, executorService);
	}

	public static <T> CompletableFuture<T> of(T t) {
		return CompletableFuture.supplyAsync(() -> t);
	}

	private CompletableFutures() {
		// Utility class
	}
}
