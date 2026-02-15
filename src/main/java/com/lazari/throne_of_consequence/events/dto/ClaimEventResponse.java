package com.lazari.throne_of_consequence.events.dto;

import java.util.Map;

public record ClaimEventResponse(
        long eventInstanceId,
        String eventKey,
        Map<String, Object> payload
) {}