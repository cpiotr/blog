package pl.ciruk.blog.jms;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

class WhenReceived<T> {
    private final Gateway<T> gateway;

    private final Predicate<T> messageMatcher;

    private final List<Consumer<T>> consumers = new ArrayList<>();

    WhenReceived(Gateway<T> mockDestination, Predicate<T> messageMatcher) {
        this.gateway = mockDestination;
        this.messageMatcher = messageMatcher;
    }

    public WhenReceived<T> thenSend(String destination, String message) {
        consumers.add(__ -> gateway.send(destination, message));
        return this;
    }

    public WhenReceived<T> thenSend(String destination, Function<T, String> messageOperator) {
        consumers.add(message -> gateway.send(destination, messageOperator.apply(message)));
        return this;
    }

    public WhenReceived<T> thenDo(Runnable runnable) {
        consumers.add(__ -> runnable.run());
        return this;
    }

    public WhenReceived<T> thenConsume(Consumer<T> messageConsumer) {
        consumers.add(messageConsumer);
        return this;
    }

    public void onMessage(T message) {
        if (messageMatcher.test(message)) {
            consumers.forEach(c -> c.accept(message));
        }
    }
}
