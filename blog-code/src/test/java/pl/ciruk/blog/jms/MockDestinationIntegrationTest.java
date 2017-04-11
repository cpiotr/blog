package pl.ciruk.blog.jms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import pl.ciruk.blog.mq.MQConfiguration;

import javax.inject.Inject;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MockDestinationIntegrationTest.TestConfiguration.class})
@EnableAutoConfiguration
@EnableJms
public class MockDestinationIntegrationTest {
    private static final String FIRST_MESSAGE = "Alice";
    private static final String SECOND_MESSAGE = "Another";
    private static final String RESPONSE_QUEUE = "ResponseQueue";

    @Inject
    private JmsTemplate jmsTemplate;

    @Inject
    private MockDestination<String> mockDestination;

    @Test
    public void shouldPrintTwoReceivedMessagesAndRespondToOne() throws Exception {
        mockDestination
                .whenReceived(message -> message.equals(FIRST_MESSAGE))
                .thenSend(RESPONSE_QUEUE, message -> message + " acknowledged");
        mockDestination
                .whenReceived(message -> message.startsWith("A"))
                .thenConsume(message -> System.out.println("Received: " + message));

        send(FIRST_MESSAGE);
        send(SECOND_MESSAGE);

        System.out.println(jmsTemplate.receiveAndConvert(RESPONSE_QUEUE));
    }

    private void send(String firstMessage) {
        waitAMoment();
        jmsTemplate.convertAndSend("RequestQueue", firstMessage);
    }

    private void waitAMoment() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            Thread.interrupted();
        }
    }

    @Configuration
    static class TestConfiguration extends MQConfiguration {
        @Bean
        Gateway<String> mockDestination(JmsTemplate jmsTemplate) {
            return new MockDestination<>(jmsTemplate, Function.identity());
        }
    }

}
