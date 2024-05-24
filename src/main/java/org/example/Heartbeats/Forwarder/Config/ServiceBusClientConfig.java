package org.example.Heartbeats.Forwarder.Config;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClientBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class ServiceBusClientConfig {

    @Value("${service-bus.connection-string}")
    private String connectionString;
    private String topic = "heartbeat-topic";
    @Bean
    public ServiceBusSenderClient senderClient() {
        return new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .topicName(topic)
                .buildClient();
    }

    @Bean
    public ServiceBusAdministrationClient adminClient() {
        return new ServiceBusAdministrationClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }

}


