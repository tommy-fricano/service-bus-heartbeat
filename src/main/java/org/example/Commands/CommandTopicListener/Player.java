package org.example.Commands.CommandTopicListener;

import com.azure.messaging.servicebus.*;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
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

            String storeId = "404111";
            String playerCode = "uswest-" + storeId + "-500";
            String jsonMessage = "{\"playerCode\": \"" + playerCode + "\", \"timestamp\": \"" + System.currentTimeMillis() + "\"}";

            MediaType JSON = MediaType.get("application/json; charset=utf-8");

            RequestBody body = RequestBody.create(jsonMessage, JSON);

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://localhost:8080/heartbeat")
                    .post(body)
                    .build();

            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

            executorService.scheduleAtFixedRate(() -> {
                try {
                    client.newCall(request).execute();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, 0, 5, TimeUnit.SECONDS);


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

