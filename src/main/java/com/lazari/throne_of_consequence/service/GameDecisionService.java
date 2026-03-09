package com.lazari.throne_of_consequence.service;

import com.lazari.throne_of_consequence.dto.AiDecisionPayload;
import com.lazari.throne_of_consequence.dto.ConsequenceDto;
import com.lazari.throne_of_consequence.dto.ResolveDecisionRequest;
import com.lazari.throne_of_consequence.dto.ResolveDecisionResponse;
import com.lazari.throne_of_consequence.model.DecisionType;
import org.springframework.stereotype.Service;

@Service
public class GameDecisionService {

    private static final ConsequenceDto NONE_CONSEQUENCE = new ConsequenceDto(
            "Vorbe nebunesti",
            "Curtea murmura nelinistita, iar increderea in tron se clatina.",
            -1, -1, -1, -1
    );

    private final OllamaClient ollamaClient;
    private final PromptFactory promptFactory;

    public GameDecisionService(OllamaClient ollamaClient, PromptFactory promptFactory) {
        this.ollamaClient = ollamaClient;
        this.promptFactory = promptFactory;
    }

    public ResolveDecisionResponse resolve(ResolveDecisionRequest request) {
        String systemPrompt = promptFactory.buildSystemPrompt();
        String userPrompt = promptFactory.buildUserPrompt(
                request.event(),
                request.optionA(),
                request.optionB(),
                request.playerInput()
        );

        AiDecisionPayload ai = ollamaClient.classifyDecision(systemPrompt, userPrompt);

        DecisionType decision = normalize(ai.decision());

        ConsequenceDto selected = switch (decision) {
            case A -> request.optionA();
            case B -> request.optionB();
            case NONE -> NONE_CONSEQUENCE;
        };

        String narrative = normalizeNarrative(ai.narrative(), decision);

        ConsequenceDto finalConsequence = new ConsequenceDto(
                selected.title(),
                narrative,
                selected.religion(),
                selected.population(),
                selected.army(),
                selected.money()
        );

        return new ResolveDecisionResponse(
                decision.name(),
                finalConsequence
        );
    }

    private DecisionType normalize(DecisionType decision) {
        return decision == null ? DecisionType.NONE : decision;
    }

    private String normalizeNarrative(String narrative, DecisionType decision) {
        if (narrative != null && !narrative.isBlank()) {
            return narrative.strip();
        }

        return switch (decision) {
            case A -> "Regele inclina spre prima cale, iar curtea se supune poruncii sale.";
            case B -> "Regele alege a doua cale, iar sfetnicii isi pleaca fruntile.";
            case NONE -> "Regele graieste atat de ciudat incat sala amuteste. Pana si bufonul pare ingrijorat.";
        };
    }
}