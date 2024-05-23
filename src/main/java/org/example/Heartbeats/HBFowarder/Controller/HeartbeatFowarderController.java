package org.example.Heartbeats.HBFowarder.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.Heartbeats.HBFowarder.Service.HeartbeatService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class HeartbeatFowarderController {

    private final HeartbeatService heartbeatService;

    public HeartbeatFowarderController(HeartbeatService heartbeatService) {
        this.heartbeatService = heartbeatService;
    }

    @PostMapping(path = "/heartbeat")
    public ResponseEntity<String> postPayloadToHeartbeatTopic(@RequestBody String payload) throws JsonProcessingException {
        heartbeatService.forwardMessage(payload);
        return ResponseEntity.ok(payload);
    }
}
