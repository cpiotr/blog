package pl.ciruk.blog.jms;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

class WhenReceived<T> {
    private final MockDestination<T> mockDestination;

    private final Predicate<T> messageMatcher;

    WhenReceived(MockDestination<T> mockDestination, Predicate<T> messageMatcher) {
        this.mockDestination = mockDestination;
        this.messageMatcher = messageMatcher;
    }

    public void thenSend(String destination, String message) {
        mockDestination.registerResponse(messageMatcher, new ThenSend<>(destination, message));
    }

    void thenSend(String destination, Function<T, String> messageOperator) {
        mockDestination.registerResponse(messageMatcher, new ThenSend<>(destination, messageOperator));
    }

    public void thenDo(Runnable runnable) {
        mockDestination.registerResponse(messageMatcher, runnable);
    }

    void thenConsume(Consumer<T> messageConsumer) {
        mockDestination.registerResponse(messageMatcher, messageConsumer);
    }
}
