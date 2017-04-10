package pl.ciruk.blog.noswitch;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ConsumerWithPredicates<U> {
	List<Object> consumers = Lists.newArrayList();

	public <T extends Consumer<U> & Predicate<U>> void add(T consumer) {
		consumers.add(consumer);
	}

	public void add(Predicate<U> predicate, Consumer<U> consumer) {
		consumers.add(new TestAndConsume<>(predicate, consumer));
	}

	public void add(Predicate<U> predicate, Runnable action) {
		consumers.add(new TestAndConsume<>(predicate, __ -> action.run()));
	}

	public void consume(U element) {
		consumers.stream()
				.map(c -> (Consumer<U> & Predicate<U>) c)
				.filter(c -> c.test(element))
				.forEach(c -> c.accept(element));
	}

	static class TestAndConsume<T> implements Consumer<T>, Predicate<T> {
	    private final Predicate<T> predicate;

	    private final Consumer<T> consumer;

        TestAndConsume(Predicate<T> predicate, Consumer<T> consumer) {
            this.predicate = predicate;
            this.consumer = consumer;
        }

        @Override
        public void accept(T t) {
            consumer.accept(t);
        }

        @Override
        public boolean test(T t) {
            return predicate.test(t);
        }
    }
}
