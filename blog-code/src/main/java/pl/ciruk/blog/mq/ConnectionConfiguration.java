package pl.ciruk.blog.mq;

import com.ibm.mq.jms.MQXAConnectionFactory;
import com.ibm.msg.client.wmq.WMQConstants;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;

@Configuration
@EnableConfigurationProperties(MQProperties.class)
@EnableJms
public class ConnectionConfiguration {
    @Inject
    MQProperties properties;

    @Bean
    public ConnectionFactory connectionFactory() {
        MQXAConnectionFactory factory = null;
        try {
            factory = new MQXAConnectionFactory();
            factory.setHostName(properties.getHost());
            factory.setPort(properties.getPort());
            factory.setQueueManager(properties.getQueueManager());
            factory.setChannel(properties.getChannel());
            factory.setTransportType(WMQConstants.WMQ_CM_CLIENT);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }

        return factory;
    }

}
