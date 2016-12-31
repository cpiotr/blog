package pl.ciruk.blog.mq;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "pl.ciruk.blog.mq")
@Data
public class MQProperties {
    String queueManager;
    String host;
    int port;
    String channel;
    String incomingQueue;
    String outgoingQueue;
}
