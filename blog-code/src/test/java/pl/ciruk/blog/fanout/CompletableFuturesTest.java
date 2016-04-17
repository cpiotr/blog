package pl.ciruk.blog.fanout;

import com.google.common.collect.ImmutableList;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
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
}