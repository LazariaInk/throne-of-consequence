package com.lazari.throne_of_consequence.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OllamaGenerateResponse(
        String model,
        String created_at,
        String response,
        boolean done
) {
}