package org.example.Config;

import com.azure.core.annotation.Get;
import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClientBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

@Getter
@Setter
@Configuration
public class ServiceBusClientConfig {

    @Value("${service-bus.connection-string}")
    private String connectionString;
    private String hearbeatTopic = "heartbeat-topic";
    private String commandTopic = "command-topic";

    @Bean
    public ServiceBusSenderClient commandSenderClient() {
        return new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .topicName(commandTopic)
                .buildClient();
    }
    @Bean
    public ServiceBusSenderClient heartbeatSenderClient() {
        return new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .topicName(hearbeatTopic)
                .buildClient();
    }
    @Bean
    public ServiceBusAdministrationClient adminClient() {
        return new ServiceBusAdministrationClientBuilder()
                .connectionString(connectionString)
                .buildClient();
    }
}
