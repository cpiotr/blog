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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.awaitility.Awaitility.await;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MockDestinationIntegrationTest.TestConfiguration.class})
@EnableAutoConfiguration
@EnableJms
public class MockDestinationIntegrationTest {
    @Inject
    private JmsTemplate jmsTemplate;

    @Inject
    private MockDestination<String> mockDestination;

    @Test
    public void shouldName() throws Exception {
        mockDestination
                .whenReceived(messsage -> messsage.equals("Ala"))
                .thenSend("ResponseQueue", message -> message + " Ma kota");
        mockDestination
                .whenReceived(messsage -> messsage.startsWith("A"))
                .thenConsume(message -> System.out.println("Received: " + message));

        await().atLeast(1, TimeUnit.SECONDS);
        jmsTemplate.convertAndSend("RequestQueue", "Ala");

        await().atLeast(1, TimeUnit.SECONDS);
        jmsTemplate.convertAndSend("RequestQueue", "Arka");

        System.out.println(jmsTemplate.receiveAndConvert("ResponseQueue"));
    }

    @Configuration
    static class TestConfiguration extends MQConfiguration {
        List<String> receivedMessages = new CopyOnWriteArrayList<>();

        @Bean
        public MockDestination<String> mockDestination(JmsTemplate jmsTemplate) {
            return new MockDestination<>(jmsTemplate, Function.identity());
        }
    }

}
