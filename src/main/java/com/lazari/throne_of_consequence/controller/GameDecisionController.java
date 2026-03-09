package com.lazari.throne_of_consequence.controller;

import com.lazari.throne_of_consequence.dto.ResolveDecisionRequest;
import com.lazari.throne_of_consequence.dto.ResolveDecisionResponse;
import com.lazari.throne_of_consequence.service.GameDecisionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameDecisionController {

    private final GameDecisionService gameDecisionService;

    public GameDecisionController(GameDecisionService gameDecisionService) {
        this.gameDecisionService = gameDecisionService;
    }

    @PostMapping("/resolve")
    public ResolveDecisionResponse resolve(@Valid @RequestBody ResolveDecisionRequest request) {
        System.out.println("A intrat in /api/game/resolve");
        System.out.println("playerInput = " + request.playerInput());
        return gameDecisionService.resolve(request);
    }
}