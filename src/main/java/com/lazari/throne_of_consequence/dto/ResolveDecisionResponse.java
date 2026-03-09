package com.lazari.throne_of_consequence.dto;

import com.lazari.throne_of_consequence.model.DecisionType;

public record ResolveDecisionResponse(
        String eventId,
        DecisionType decision,
        String narrative,
        String aiReason,
        ConsequenceDto consequence
) {
}