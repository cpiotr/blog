package pl.ciruk.blog.jms;

import java.util.function.Function;

class ThenSend<T> {
    private final String destination;

    private final Function<T, String> messageOperator;

    ThenSend(String destination, Function<T, String> messageOperator) {
        this.destination = destination;
        this.messageOperator = messageOperator;
    }

    ThenSend(String destination, String response) {
        this.destination = destination;
        this.messageOperator = __ -> response;
    }

    String getDestination() {
        return destination;
    }

    String apply(T message) {
        return messageOperator.apply(message);
    }
}
