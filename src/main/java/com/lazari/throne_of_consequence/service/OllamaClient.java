package com.lazari.throne_of_consequence.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lazari.throne_of_consequence.config.OllamaProperties;
import com.lazari.throne_of_consequence.dto.AiDecisionPayload;
import com.lazari.throne_of_consequence.dto.OllamaGenerateRequest;
import com.lazari.throne_of_consequence.dto.OllamaGenerateResponse;
import com.lazari.throne_of_consequence.exception.OllamaIntegrationException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OllamaClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final OllamaProperties properties;

    public OllamaClient(ObjectMapper objectMapper, OllamaProperties properties) {
        this.objectMapper = objectMapper;
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .build();
    }

    public AiDecisionPayload classifyDecision(String systemPrompt, String userPrompt) {
        try {
            OllamaGenerateRequest request = new OllamaGenerateRequest(
                    properties.getModel(),
                    userPrompt,
                    systemPrompt,
                    false,
                    null,
                    properties.getKeepAlive(),
                    false
            );

            String rawBody = restClient.post()
                    .uri("/api/generate")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(request)
                    .retrieve()
                    .body(String.class);

            System.out.println("=== OLLAMA RAW BODY ===");
            System.out.println(rawBody);

            if (rawBody == null || rawBody.isBlank()) {
                throw new OllamaIntegrationException("Empty raw response from Ollama");
            }

            OllamaGenerateResponse response = objectMapper.readValue(rawBody, OllamaGenerateResponse.class);

            if (response == null || response.response() == null || response.response().isBlank()) {
                throw new OllamaIntegrationException("Empty response from Ollama. Raw body: " + rawBody);
            }

            String modelText = response.response().trim();
            System.out.println("=== OLLAMA MODEL TEXT ===");
            System.out.println(modelText);

            String json = extractJsonObject(modelText);
            return objectMapper.readValue(json, AiDecisionPayload.class);

        } catch (Exception ex) {
            throw new OllamaIntegrationException("Failed to call Ollama: " + ex.getMessage(), ex);
        }
    }

    private String extractJsonObject(String text) {
        int start = text.indexOf('{');
        int end = text.lastIndexOf('}');
        if (start >= 0 && end > start) {
            return text.substring(start, end + 1);
        }
        throw new OllamaIntegrationException("Model response does not contain valid JSON: " + text);
    }
}