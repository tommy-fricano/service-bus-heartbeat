package org.example.Heartbeats.Forwarder.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.Heartbeats.Forwarder.Service.HeartbeatForwarderService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class HeartbeatFowarderController {
    private final HeartbeatForwarderService heartbeatForwarderService;

    public HeartbeatFowarderController(HeartbeatForwarderService heartbeatForwarderService) {
        this.heartbeatForwarderService = heartbeatForwarderService;
    }

    @PostMapping(path = "/heartbeat")
    public ResponseEntity<String> postPayloadToCommandTopic(@RequestBody String payload) throws JsonProcessingException {
        heartbeatForwarderService.forwardMessage(payload);
        return ResponseEntity.ok(payload);
    }
}
