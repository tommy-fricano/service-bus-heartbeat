package org.example.Heartbeats.HBFowarder.Service;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class HeartbeatService {

    private final ServiceBusSenderClient senderClient;
    private final ServiceBusAdministrationClient adminClient;
    private final String HeartbeatTopic = "heartbeat-topic";
    private ObjectMapper objectMapper;

    public HeartbeatService(ServiceBusSenderClient senderClient, ServiceBusAdministrationClient adminClient) {
        this.senderClient = senderClient;
        this.adminClient = adminClient;
    }

    public void forwardMessage(String heartbeat) throws JsonProcessingException {
        JsonNode heartbeatNode = objectMapper.readTree(heartbeat);
        String storeId = heartbeatNode.get("playerCode").asText().split("-")[1];

        ServiceBusMessage serviceBusMessage = new ServiceBusMessage(heartbeat)
                .setSubject("Command")
                .setSessionId(storeId);

        Map<String, Object> properties = serviceBusMessage.getApplicationProperties();
        properties.put("storeId", storeId);

        senderClient.sendMessage(serviceBusMessage);
    }
}
