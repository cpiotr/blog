package pl.ciruk.blog.fanout;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static pl.ciruk.blog.fanout.CompletableFutures.fanOut;

public class CompletableFuturesTest {
	@Test
	public void shouldExecuteAllFunctionsFromMappings() throws Exception {
		// Given
		CompletableFuture<String> future = CompletableFuture.completedFuture("SampleText");

		// When
		CompletableFuture<Stream<Integer>> actualFuture = future.thenCompose(
				fanOut(ImmutableList.of(
						text -> 1,
						text -> 2,
						text -> 4
				))
		);
		CompletableFuture<Integer> actualSum = actualFuture.thenApply(
				stream -> stream.reduce((a, b) -> a + b).orElse(-1)
		);

		// Then
		assertThat(actualSum.get(), is(equalTo(7)));
	}
	@Test
	public void shouldExecuteLongRunningOutliers() throws Exception {
		// Given
		CompletableFuture<String> future = CompletableFuture.completedFuture("SampleText");

		// When
		CompletableFuture<Stream<Integer>> actualFuture = future.thenCompose(
				fanOut(ImmutableList.of(
						text -> 1,
						text -> 2,
						text -> {
							waitThreeSecond();
							return 4;
						}
				))
		);
		CompletableFuture<Integer> actualSum = actualFuture.thenApply(
				stream -> stream.reduce((a, b) -> a + b).orElse(-1)
		);

		// Then
		assertThat(actualSum.get(), is(equalTo(7)));
	}

	@Test
	public void shouldExecuteAllFunctionsAtTheSameTime() throws Exception {
		// Given
		CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> "SampleText");

		// When
		ExecutorService pool = Executors.newFixedThreadPool(4);
		CompletableFuture<Stream<Integer>> actualFuture = future.thenComposeAsync(
				fanOut(ImmutableList.of(
						text -> {
							waitThreeSecond();
							return 1;
						},
						text -> {
							waitThreeSecond();
							return 2;
						},
						text -> {
							waitThreeSecond();
							return 4;
						})
				)
		);

		// Then
		try {
			actualFuture.get(4, TimeUnit.SECONDS);
		} catch (TimeoutException e) {
			fail("Should have finished in less than four seconds");
		}
	}

	void waitThreeSecond() {
		try {
			Thread.sleep(3_000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}
	}
}