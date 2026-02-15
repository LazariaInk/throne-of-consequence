package com.lazari.throne_of_consequence.events.dto;

import java.util.Map;

public record ReplyResponse(
        long eventInstanceId,
        Map<String, Integer> effects,
        String consequence,
        Map<String, Integer> newStats
) {}
