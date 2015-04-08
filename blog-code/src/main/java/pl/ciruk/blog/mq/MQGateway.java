package pl.ciruk.blog.mq;

import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;

import javax.inject.Inject;
import javax.inject.Named;
import javax.jms.JMSException;
import javax.jms.TextMessage;
import javax.transaction.Transactional;
import java.util.function.Consumer;

@Named
@Transactional
@lombok.extern.slf4j.Slf4j
public class MQGateway {
	@Inject
	@Named("JmsTemplate")
	JmsTemplate jmsTemplate;

	@Inject
	private MQConfiguration.MQProperties properties;

	@JmsListener(destination = "${pl.ciruk.blog.mq.incoming-queue}", containerFactory = "DefaultJmsListenerContainerFactory")
	public void onMessage(TextMessage message) throws JMSException {
		log.info("onMessage");
		log.debug("onMessage - Message: {}", message);

		Consumer<TextMessage> handler = msg -> {};

		handler.accept(message);
	}

	public void send(String message) {
		log.info("send");
		log.debug("send - Message: {}", message);

		jmsTemplate.convertAndSend(properties.getOutgoingQueue(), message);
	}
}
