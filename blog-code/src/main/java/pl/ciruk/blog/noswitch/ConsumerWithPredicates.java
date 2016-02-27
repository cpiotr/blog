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

	public void consume(U element) {
		consumers.stream()
				.map(c -> (Consumer<U> & Predicate<U>) c)
				.filter(c -> c.test(element))
				.forEach(c -> c.accept(element));
	}
}
