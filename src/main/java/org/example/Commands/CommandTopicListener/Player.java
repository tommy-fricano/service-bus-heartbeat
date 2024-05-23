package org.example.Commands.CommandTopicListener;

import com.azure.messaging.servicebus.*;
import org.springframework.beans.factory.annotation.Value;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


import java.util.Map;
import java.util.concurrent.TimeUnit;


public class Player {

        @Value("${service-bus.connection-string}")
        private static String CONNECTION_STRING;
        private static final String TOPIC_NAME = "heartbeat-topic";
        private static final String SUBSCRIPTION_NAME = "heartbeat-sub";

        public static void main(String[] args) {

            ServiceBusSenderClient senderClient = new ServiceBusClientBuilder()
                    .connectionString(CONNECTION_STRING)
                    .sender()
                    .topicName(TOPIC_NAME)
                    .buildClient();

            String storeId = "404111";
            String playerCode = "us-" + storeId + "-500";
            String jsonMessage = "{\"playerCode\": \"" + playerCode + "\", \"timestamp\": \"" + System.currentTimeMillis() + "\"}";

            ServiceBusMessage serviceBusMessage = new ServiceBusMessage(jsonMessage)
                    .setSubject("Heartbeat")
                    .setSessionId(storeId);

            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

            executorService.scheduleAtFixedRate(() -> senderClient.sendMessage(serviceBusMessage), 0, 5, TimeUnit.SECONDS);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Shutting down...");
                executorService.shutdown();
                senderClient.close();
                System.exit(0);
            }));

//            ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
//                    .connectionString(CONNECTION_STRING)
//                    .processor()
//                    .topicName(TOPIC_NAME)
//                    .subscriptionName(SUBSCRIPTION_NAME)
//                    .processMessage(Player::processMessage)
//                    .processError(Player::processError)
//                    .buildProcessorClient();
//
//            processorClient.start();
//
//            System.out.println("Listening for messages on topic: " + TOPIC_NAME + " and subscription: " + SUBSCRIPTION_NAME);
//
//            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//                System.out.println("Closing the processor client...");
//                processorClient.close();
//            }));
        }

        private static void processMessage(ServiceBusReceivedMessageContext context) {
            System.out.printf("Received message: Sequence #: %s. Contents: %s%n",
                    context.getMessage().getSequenceNumber(),
                    context.getMessage().getBody().toString());

            context.complete();
        }

        private static void processError(ServiceBusErrorContext context) {
            System.err.printf("Error occurred while receiving messages: %s%n", context.getException().getMessage());
        }
}

