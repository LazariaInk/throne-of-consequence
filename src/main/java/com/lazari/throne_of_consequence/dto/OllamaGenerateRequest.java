package com.lazari.throne_of_consequence.dto;

import com.fasterxml.jackson.databind.JsonNode;


import com.fasterxml.jackson.databind.JsonNode;

public record OllamaGenerateRequest(
        String model,
        String prompt,
        String system,
        boolean stream,
        JsonNode format,
        String keep_alive,
        Boolean think
) {
}