package org.example.Service;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;
import com.azure.messaging.servicebus.administration.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class HeartbeatForwarderService {

    private final ServiceBusSenderClient heartbeatSenderClient;
    private final ServiceBusAdministrationClient adminClient;
    private final String HeartbeatTopic = "heartbeat-topic";
    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public HeartbeatForwarderService(ServiceBusSenderClient heartbeatSenderClient,
                                     ServiceBusAdministrationClient adminClient) {
        this.heartbeatSenderClient = heartbeatSenderClient;
        this.adminClient = adminClient;
    }

    public void forwardMessage(String heartbeat) throws JsonProcessingException {
        JsonNode heartbeatNode = objectMapper.readTree(heartbeat);
        System.out.println(heartbeat);
        String storeId = heartbeatNode.get("playerCode").asText().split("-")[1];
        String tenant = heartbeatNode.get("playerCode").asText().split("-")[0];
        ServiceBusMessage serviceBusMessage = new ServiceBusMessage(heartbeat)
                .setSubject("Heartbeat")
                .setSessionId(storeId);

        Map<String, Object> properties = serviceBusMessage.getApplicationProperties();
        properties.put("playerCode", tenant + "-" + storeId);

        createSubIfNoneExist(tenant);

        heartbeatSenderClient.sendMessage(serviceBusMessage);
    }

    private void createSubIfNoneExist(String tenant) {
        String subName = "heartbeat-sub-" + tenant;
        if (!adminClient.getSubscriptionExists(HeartbeatTopic, subName)) {
            adminClient.createSubscription(HeartbeatTopic, subName,
                    new CreateSubscriptionOptions());
        }
    }

    @PreDestroy
    public void cleanup() {
        // Close the Service Bus Sender client when the application is shutting down
        if (heartbeatSenderClient != null) {
            heartbeatSenderClient.close();
        }
    }
}