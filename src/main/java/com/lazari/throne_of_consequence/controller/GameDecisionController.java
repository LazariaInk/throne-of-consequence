package com.lazari.throne_of_consequence.controller;

import com.lazari.throne_of_consequence.dto.ResolveDecisionRequest;
import com.lazari.throne_of_consequence.dto.ResolveDecisionResponse;
import com.lazari.throne_of_consequence.service.GameDecisionService;
import com.lazari.throne_of_consequence.service.OpenAiGameDecisionService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "*")
public class GameDecisionController {

    private final GameDecisionService gameDecisionService;
    private final OpenAiGameDecisionService openAiGameDecisionService;

    public GameDecisionController(
            GameDecisionService gameDecisionService,
            OpenAiGameDecisionService openAiGameDecisionService
    ) {
        this.gameDecisionService = gameDecisionService;
        this.openAiGameDecisionService = openAiGameDecisionService;
    }

//    @PostMapping("/resolve")
//    public ResolveDecisionResponse resolve(@Valid @RequestBody ResolveDecisionRequest request) {
//        return gameDecisionService.resolve(request);
//    }

    @PostMapping("/resolve")
    public ResolveDecisionResponse resolveOpenAi(@Valid @RequestBody ResolveDecisionRequest request) {
        return openAiGameDecisionService.resolve(request);
    }
}