package com.lazari.throne_of_consequence.events;


import com.lazari.throne_of_consequence.events.dto.ClaimEventResponse;
import com.lazari.throne_of_consequence.events.dto.ReplyRequest;
import com.lazari.throne_of_consequence.events.dto.ReplyResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService service;

    public EventController(EventService service) {
        this.service = service;
    }

    @PostMapping("/claim")
    public ClaimEventResponse claim(@RequestParam String playerId) {
        return service.claim(playerId);
    }

    @PostMapping("/{eventInstanceId}/reply")
    public ReplyResponse reply(
            @RequestParam String playerId,
            @PathVariable long eventInstanceId,
            @RequestBody ReplyRequest body
    ) {
        return service.reply(playerId, eventInstanceId, body.reply());
    }
}
