package org.example.portal;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor
public class ProcessMessages implements Runnable {
    //      todo make sure to add connection string manually
    private static final String TOPIC_NAME = "heartbeat-topic";
    private static final String SUBSCRIPTION_NAME = "heartbeat-sub-uswest";
    protected static List<String> heartbeats = Collections.synchronizedList(new ArrayList<>());

    @Override
    public void run() {

        ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .processor()
                .topicName(TOPIC_NAME)
                .subscriptionName(SUBSCRIPTION_NAME)
                .processMessage(ProcessMessages::processMessage)
                .processError(ProcessMessages::processError)
                .buildProcessorClient();

        processorClient.start();

        System.out.println("Listening for messages on topic: " + TOPIC_NAME + " and subscription: " + SUBSCRIPTION_NAME);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Closing the processor client...");
            processorClient.close();
            System.exit(0);
        }));
    }

    private static void processMessage(ServiceBusReceivedMessageContext context) {
        heartbeats.add(context.getMessage().getBody().toString());

        context.complete();
    }

    private static void processError(ServiceBusErrorContext context) {
//        System.err.printf("Error occurred while receiving messages: %s%n", context.getException().getMessage());
    }
}
