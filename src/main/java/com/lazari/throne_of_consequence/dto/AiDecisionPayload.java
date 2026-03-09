package com.lazari.throne_of_consequence.dto;

import com.lazari.throne_of_consequence.model.DecisionType;

public record AiDecisionPayload(
        DecisionType decision,
        String narrative,
        String reason
) {
}