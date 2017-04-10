package pl.ciruk.blog.jms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
class MockDestination<T> implements Gateway<T> {
    private final List<WhenReceived<T>> listeners = new ArrayList<>();

    private final JmsTemplate jmsTemplate;

    private final Function<String, T> fromTextToMessageConverter;

    MockDestination(JmsTemplate jmsTemplate, Function<String, T> fromTextToMessageConverter) {
        this.jmsTemplate = jmsTemplate;
        this.fromTextToMessageConverter = fromTextToMessageConverter;
    }

    @Override
    public void receive(T message) {
        listeners.forEach(consumer -> consumer.onMessage(message));
    }

    @Override
    public void send(String destination, String message) {
        jmsTemplate.convertAndSend(destination, message);
    }

    @JmsListener(destination = "RequestQueue", containerFactory = "defaultJmsListenerContainerFactory")
    private void onMessage(TextMessage message) throws JMSException {
        log.info("onMessage");
        log.debug("onMessage - Message: {}", message);

        try {
            String messageText = message.getText();

            receive(fromTextToMessageConverter.apply(messageText));
        } catch (JMSException e) {
            log.error("Cannot consume message: {}", message, e);
        }
    }

    public WhenReceived<T> whenReceived(Predicate<T> messageMatcher) {
        WhenReceived<T> whenReceived = new WhenReceived<>(this, messageMatcher);
        listeners.add(whenReceived);
        return whenReceived;
    }

}
