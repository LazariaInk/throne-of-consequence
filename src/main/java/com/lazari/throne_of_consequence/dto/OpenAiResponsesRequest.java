package com.lazari.throne_of_consequence.dto;

import java.util.Map;

public record OpenAiResponsesRequest(
        String model,
        String instructions,
        String input,
        Map<String, Object> text
) {
}