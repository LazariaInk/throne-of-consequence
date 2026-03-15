package com.lazari.throne_of_consequence.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lazari.throne_of_consequence.dto.AiDecisionPayload;
import com.lazari.throne_of_consequence.dto.OpenAiResponsesRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class OpenAiClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;
    private final String model;

    public OpenAiClient(
            RestClient.Builder restClientBuilder,
            ObjectMapper objectMapper,
            @Value("${openai.base-url}") String baseUrl,
            @Value("${openai.api-key}") String apiKey,
            @Value("${openai.model:gpt-4.1-mini}") String model
    ) {
        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .build();
        this.objectMapper = objectMapper;
        this.model = model;
    }

    public AiDecisionPayload classifyDecision(String systemPrompt, String userPrompt) {
        try {
            OpenAiResponsesRequest request = new OpenAiResponsesRequest(
                    model,
                    systemPrompt,
                    userPrompt,
                    Map.of(
                            "format", Map.of(
                                    "type", "json_schema",
                                    "name", "ai_decision_payload",
                                    "strict", true,
                                    "schema", Map.of(
                                            "type", "object",
                                            "additionalProperties", false,
                                            "properties", Map.of(
                                                    "decision", Map.of(
                                                            "type", "string",
                                                            "enum", new String[]{"A", "B", "C"}
                                                    ),
                                                    "narrative", Map.of(
                                                            "type", "string"
                                                    ),
                                                    "reason", Map.of(
                                                            "type", "string"
                                                    )
                                            ),
                                            "required", new String[]{"decision", "narrative", "reason"}
                                    )
                            )
                    )
            );

            JsonNode response = restClient.post()
                    .uri("/responses")
                    .body(request)
                    .retrieve()
                    .body(JsonNode.class);

            System.out.println("OPENAI RAW RESPONSE:");
            System.out.println(response != null ? response.toPrettyString() : "null");

            if (response == null) {
                return null;
            }

            String outputText = extractOutputText(response);
            System.out.println("OPENAI output_text: " + outputText);

            if (outputText == null || outputText.isBlank()) {
                System.out.println("output_text is null or blank");
                return null;
            }

            AiDecisionPayload payload = objectMapper.readValue(outputText, AiDecisionPayload.class);
            System.out.println("PARSED decision: " + payload.decision());
            System.out.println("PARSED narrative: " + payload.narrative());
            System.out.println("PARSED reason: " + payload.reason());

            return payload;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String extractOutputText(JsonNode response) {
        JsonNode output = response.path("output");
        if (!output.isArray()) {
            return null;
        }

        for (JsonNode item : output) {
            JsonNode content = item.path("content");
            if (!content.isArray()) {
                continue;
            }

            for (JsonNode part : content) {
                if ("output_text".equals(part.path("type").asText())) {
                    String text = part.path("text").asText(null);
                    if (text != null && !text.isBlank()) {
                        return text;
                    }
                }
            }
        }

        return null;
    }
}