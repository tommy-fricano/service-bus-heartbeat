package org.example.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.example.Service.CommandForwarderService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class CommandFowarderController {
    private final CommandForwarderService commandForwarderService;

    public CommandFowarderController(CommandForwarderService commandForwarderService) {
        this.commandForwarderService = commandForwarderService;
    }

    @PostMapping(path = "/command")
    public ResponseEntity<String> postPayloadToCommandTopic(@RequestBody String payload) throws JsonProcessingException {
        commandForwarderService.forwardMessage(payload);
        return ResponseEntity.ok(payload);
    }
}
