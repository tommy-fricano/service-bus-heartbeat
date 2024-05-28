package org.example.Service;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.azure.messaging.servicebus.administration.ServiceBusAdministrationClient;
import com.azure.messaging.servicebus.administration.models.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.Map;

import static java.lang.String.format;

@Service
public class CommandForwarderService {

    private final ServiceBusSenderClient senderClient;
    private final ServiceBusAdministrationClient adminClient;
    private final String CommandTopic = "command-topic";
    private ObjectMapper objectMapper = new ObjectMapper();

    public CommandForwarderService(ServiceBusSenderClient commandSenderClient,
                                   ServiceBusAdministrationClient adminClient) {
        this.senderClient = commandSenderClient;
        this.adminClient = adminClient;
    }

    public void forwardMessage(String command) throws JsonProcessingException {
        JsonNode commandNode = objectMapper.readTree(command);
        String storeId = commandNode.get("playerCode").asText().split("-")[1];
        String playerNum = commandNode.get("playerCode").asText().split("-")[2];

        ServiceBusMessage serviceBusMessage = new ServiceBusMessage(command)
                .setSubject("Command")
                .setSessionId(storeId);

        Map<String, Object> properties = serviceBusMessage.getApplicationProperties();
        properties.put("storeId", "player-" + storeId);
        properties.put("player", storeId+ "-" + playerNum);

        createTopicAndSubIfNoneExist(storeId, playerNum);

        senderClient.sendMessage(serviceBusMessage);
    }

    private void createTopicAndSubIfNoneExist(String storeId, String playerNum) {
        String storeTopicName = "player-" + storeId.substring(0, 4);
        String subName = storeId + "-" + playerNum;

        if (!adminClient.getTopicExists(storeTopicName)) {
            adminClient.createTopic("player-" + storeId.substring(0, 4), new CreateTopicOptions().setPartitioningEnabled(true));
        }
        if (!adminClient.getSubscriptionExists(CommandTopic, storeTopicName)) {
            adminClient.createSubscription(CommandTopic, storeTopicName,
                    new CreateSubscriptionOptions().setForwardTo(storeTopicName));
            adminClient.createRule(CommandTopic, "store-filter", storeTopicName,
                    new CreateRuleOptions(new SqlRuleFilter(format("storeId LIKE '%s%%'", storeTopicName))));
            adminClient.deleteRule(CommandTopic, storeTopicName, "$Default");
        }
        if (!adminClient.getSubscriptionExists(storeTopicName, subName)) {
            adminClient.createSubscription(storeTopicName, subName);
            CorrelationRuleFilter correlationRuleFilter = new CorrelationRuleFilter();
            correlationRuleFilter.getProperties().put("player", subName);
            adminClient.createRule(storeTopicName, "player-filter", subName,
                    new CreateRuleOptions(correlationRuleFilter));
            adminClient.deleteRule(storeTopicName, subName, "$Default");
        }
    }

    @PreDestroy
    public void cleanup() {
        // Close the Service Bus Sender client when the application is shutting down
        if (senderClient != null) {
            senderClient.close();
        }
    }
}