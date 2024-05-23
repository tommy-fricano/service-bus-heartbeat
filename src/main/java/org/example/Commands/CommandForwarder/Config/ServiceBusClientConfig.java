package org.example.Commands.CommandForwarder.Config;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClientBuilder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
public class ServiceBusClientConfig {

    private String connectionString = "Endpoint=sb://ngrp-dev-servicebus.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=+B1YqYZDmgBUQNIOcd9tRomBENdWcJnRM+ASbH5ElTc=";
    private String topic = "command-topic";

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


