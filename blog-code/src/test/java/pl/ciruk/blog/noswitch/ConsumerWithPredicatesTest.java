package pl.ciruk.blog.noswitch;

import org.junit.Test;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

public class ConsumerWithPredicatesTest {

	@Test
	public void shouldAcceptDifferentInterfaces() throws Exception {
		// Given
		ConsumerWithPredicates<String> types = new ConsumerWithPredicates();

		// When
		types.add(newTestAndConsume(e -> true, e -> {}));
		types.add(newActionWithPredicate(e -> true, e -> {}));

		// Then
		assertThat(types.consumers, hasSize(2));
	}

	@Test
	public void shouldPickOnlyActionWithPredicate() throws Exception {
		// Given
		ConsumerWithPredicates<String> types = new ConsumerWithPredicates();
		Consumer<String> testConsumer = mock(Consumer.class);
		Consumer<String> actionConsumer = mock(Consumer.class);
		types.add(newTestAndConsume(e -> false, testConsumer));
		types.add(newActionWithPredicate(e -> true, actionConsumer));

		// When
		types.consume(sampleElement());

		// Then
		verifyZeroInteractions(testConsumer);
		verify(actionConsumer).accept(anyString());
	}

	@Test
	public void shouldPickOnlyTestAndConsume() throws Exception {
		// Given
		ConsumerWithPredicates<String> types = new ConsumerWithPredicates();
		Consumer<String> testConsumer = mock(Consumer.class);
		Consumer<String> actionConsumer = mock(Consumer.class);
		types.add(newTestAndConsume(e -> true, testConsumer));
		types.add(newActionWithPredicate(e -> false, actionConsumer));

		// When
		types.consume(sampleElement());

		// Then
		verifyZeroInteractions(actionConsumer);
		verify(testConsumer).accept(anyString());
	}

	@Test
	public void shouldPickAllConsumers() throws Exception {
		// Given
		ConsumerWithPredicates<String> types = new ConsumerWithPredicates();
		Consumer<String> testConsumer = mock(Consumer.class);
		Consumer<String> actionConsumer = mock(Consumer.class);
		types.add(newTestAndConsume(e -> true, testConsumer));
		types.add(newActionWithPredicate(e -> true, actionConsumer));

		// When
		types.consume(sampleElement());

		// Then
		verify(actionConsumer).accept(anyString());
		verify(testConsumer).accept(anyString());
	}

	private String sampleElement() {
		return "Sample element";
	}

	private <T> TestAndConsume<T> newTestAndConsume(Predicate<T> predicate, Consumer<T> consumer) {
		return new TestAndConsume<T>(){
			@Override
			public boolean test(T element) {
				return predicate.test(element);
			}

			@Override
			public void accept(T element) {
				consumer.accept(element);
			}
		};
	}

	private <T> ActionWithPredicate<T> newActionWithPredicate(Predicate<T> predicate, Consumer<T> consumer) {
		return new ActionWithPredicate<T>(){
			@Override
			public boolean test(T element) {
				return predicate.test(element);
			}

			@Override
			public void accept(T element) {
				consumer.accept(element);
			}
		};
	}
}