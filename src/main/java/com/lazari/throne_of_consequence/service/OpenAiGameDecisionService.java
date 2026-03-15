package com.lazari.throne_of_consequence.service;

import com.lazari.throne_of_consequence.config.OpenAiClient;
import com.lazari.throne_of_consequence.dto.AiDecisionPayload;
import com.lazari.throne_of_consequence.dto.ConsequenceDto;
import com.lazari.throne_of_consequence.dto.ResolveDecisionRequest;
import com.lazari.throne_of_consequence.dto.ResolveDecisionResponse;
import com.lazari.throne_of_consequence.model.DecisionType;
import org.springframework.stereotype.Service;

@Service
public class OpenAiGameDecisionService {

    private final OpenAiClient openAiClient;
    private final PromptFactory promptFactory;

    public OpenAiGameDecisionService(OpenAiClient openAiClient, PromptFactory promptFactory) {
        this.openAiClient = openAiClient;
        this.promptFactory = promptFactory;
    }

    public ResolveDecisionResponse resolve(ResolveDecisionRequest request) {
        String systemPrompt = promptFactory.buildSystemPrompt();
        String userPrompt = promptFactory.buildUserPrompt(
                request.event(),
                request.optionA(),
                request.optionB(),
                request.optionC(),
                request.playerInput()
        );

        AiDecisionPayload ai = openAiClient.classifyDecision(systemPrompt, userPrompt);

        DecisionType decision = normalize(ai != null ? ai.decision() : null);

        ConsequenceDto selected = switch (decision) {
            case A -> request.optionA();
            case B -> request.optionB();
            case C -> request.optionC();
        };

        String narrative = normalizeNarrative(ai != null ? ai.narrative() : null, decision, selected);

        ConsequenceDto finalConsequence = new ConsequenceDto(
                safeFallback(selected.title(), defaultTitleFor(decision)),
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
        return decision == null ? DecisionType.C : decision;
    }

    private String normalizeNarrative(String narrative, DecisionType decision, ConsequenceDto selected) {
        if (narrative != null && !narrative.isBlank()) {
            return narrative.strip();
        }

        if (selected != null && selected.text() != null && !selected.text().isBlank()) {
            return selected.text().strip();
        }

        return switch (decision) {
            case A -> "Regele inclina spre prima cale, iar curtea se supune poruncii sale.";
            case B -> "Regele alege a doua cale, iar sfetnicii isi pleaca fruntile.";
            case C -> "Regele graieste fara limpezime, iar sala ramane tulburata de nehotarare.";
        };
    }

    private String defaultTitleFor(DecisionType decision) {
        return switch (decision) {
            case A -> "Hotararea intaia";
            case B -> "Hotararea a doua";
            case C -> "Hotarare neclara";
        };
    }

    private String safeFallback(String value, String fallback) {
        return (value == null || value.isBlank()) ? fallback : value.strip();
    }
}