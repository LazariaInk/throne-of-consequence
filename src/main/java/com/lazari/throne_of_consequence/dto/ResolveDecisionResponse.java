package com.lazari.throne_of_consequence.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ResolveDecisionResponse(
        @NotBlank String resolvedOption,
        @Valid @NotNull ConsequenceDto consequence
) {
}