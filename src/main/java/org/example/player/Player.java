package org.example.player;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Player {

//      todo make sure to add and remove connection string
        private static String storeId = "404111";
        private static String tenant = "uswest";
        private static String playerNum = "1";
        private static String STORE_TOPIC_NAME = "player-" + storeId.substring(0, 4);
        private static String COMMAND_SUB_NAME = storeId + "-" + playerNum;

    public static void main(String[] args) {

            String playerCode = tenant + "-" + storeId + "-" + playerNum;
            String jsonMessage = "{\"playerCode\": \"" + playerCode + "\", \"timestamp\": \"" + System.currentTimeMillis() + "\"}";

            MediaType JSON = MediaType.get("application/json; charset=utf-8");

            RequestBody body = RequestBody.create(jsonMessage, JSON);

            OkHttpClient client = new OkHttpClient();

            Request request = new Request.Builder()
                    .url("http://localhost:8081/heartbeat")
                    .post(body)
                    .build();

            ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

            executorService.scheduleAtFixedRate(() -> {
                try (Response response = client.newCall(request).execute()) {
                    System.out.println("Response Code: " + response.code());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }, 0, 5, TimeUnit.SECONDS);


            ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
                    .connectionString(connectionString)
                    .processor()
                    .topicName(STORE_TOPIC_NAME)
                    .subscriptionName(COMMAND_SUB_NAME)
                    .processMessage(Player::processMessage)
                    .processError(Player::processError)
                    .buildProcessorClient();

            processorClient.start();

            System.out.println("Listening for messages on topic: " + STORE_TOPIC_NAME + " and subscription: " + COMMAND_SUB_NAME);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("Closing the processor client...");
                processorClient.close();
                System.exit(0);
            }));
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

