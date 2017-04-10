package pl.ciruk.blog.jms;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import pl.ciruk.blog.noswitch.ConsumerWithPredicates;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@Slf4j
class MockDestination<T> {
    private final ConsumerWithPredicates<T> listeners = new ConsumerWithPredicates<>();

    private final JmsTemplate jmsTemplate;

    private final Function<String, T> fromTextToMessageConverter;

    MockDestination(JmsTemplate jmsTemplate, Function<String, T> fromTextToMessageConverter) {
        this.jmsTemplate = jmsTemplate;
        this.fromTextToMessageConverter = fromTextToMessageConverter;
    }

    @JmsListener(destination = "RequestQueue", containerFactory = "defaultJmsListenerContainerFactory")
    private void onMessage(TextMessage message) throws JMSException {
        log.info("onMessage");
        log.debug("onMessage - Message: {}", message);

        try {
            String messageText = message.getText();

            T convertedMessage = fromTextToMessageConverter.apply(messageText);

            listeners.consume(convertedMessage);
        } catch (JMSException e) {
            log.error("Cannot consume message: {}", message, e);
        }
    }

    public void registerResponse(Predicate<T> messageMatcher, ThenSend<T> thenSend) {
        listeners.add(
                messageMatcher,
                message -> jmsTemplate.convertAndSend(thenSend.getDestination(), thenSend.apply(message)));
    }

    public void registerResponse(Predicate<T> messageMatcher, Runnable thenDo) {
        listeners.add(messageMatcher, thenDo);
    }

    public void registerResponse(Predicate<T> messageMatcher, Consumer<T> thenConsume) {
        listeners.add(messageMatcher, thenConsume);
    }

    public WhenReceived<T> whenReceived(Predicate<T> messageMatcher) {
        return new WhenReceived<>(this, messageMatcher);
    }

}
