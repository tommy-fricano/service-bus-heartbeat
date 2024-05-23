package org.example.Heartbeats.HBTopicListener;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;

public class ProcessMessages implements Runnable {

    private static final String CONNECTION_STRING = "Endpoint=sb://ngrp-dev-servicebus.servicebus.windows.net/;SharedAccessKeyName=RootManageSharedAccessKey;SharedAccessKey=+B1YqYZDmgBUQNIOcd9tRomBENdWcJnRM+ASbH5ElTc=";
    private static final String TOPIC_NAME = "heartbeat-topic";
    private static final String SUBSCRIPTION_NAME = "heartbeat-sub";

    public ProcessMessages(){}

    @Override
    public void run() {
        ServiceBusProcessorClient processorClient = new ServiceBusClientBuilder()
                .connectionString(CONNECTION_STRING)
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
        System.out.printf("Received message: Sequence #: %s. Contents: %s%n",
                context.getMessage().getSequenceNumber(),
                context.getMessage().getBody().toString());

        context.complete();
    }

    private static void processError(ServiceBusErrorContext context) {
//        System.err.printf("Error occurred while receiving messages: %s%n", context.getException().getMessage());
    }
}
