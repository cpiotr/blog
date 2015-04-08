package pl.ciruk.blog.mq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Session;

@Configuration
@EnableConfigurationProperties(MQConfiguration.MQProperties.class)
@EnableJms
public class MQConfiguration {
		@Inject
		MQConfiguration.MQProperties properties;

		@Bean(name = "DefaultJmsListenerContainerFactory")
		public DefaultJmsListenerContainerFactory provideJmsListenerContainerFactory(PlatformTransactionManager transactionManager) {
			DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
			factory.setConnectionFactory(connectionFactory());
			factory.setTransactionManager(transactionManager);
			factory.setConcurrency("5-10");
			factory.setSessionAcknowledgeMode(Session.CLIENT_ACKNOWLEDGE);
			factory.setSessionTransacted(true);
			return factory;
		}

		@Bean(name = "JmsTemplate")
		public JmsTemplate provideJmsTemplate() {
			JmsTemplate jmsTemplate = new JmsTemplate(connectionFactory());
			return jmsTemplate;
		}

		private ConnectionFactory connectionFactory() {
			MQXAConnectionFactory factory = new MQXAConnectionFactory();
			try {
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

		@ConfigurationProperties(prefix = "pl.ciruk.blog.mq")
		@Data
		public static class MQProperties {
			String queueManager;
			String host;
			int port;
			String channel;
			String incomingQueue;
			String outgoingQueue;
		}
	}
}
