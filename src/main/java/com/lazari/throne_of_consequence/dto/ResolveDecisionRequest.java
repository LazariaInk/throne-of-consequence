package com.lazari.throne_of_consequence.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ResolveDecisionRequest(
        @Valid @NotNull EventCardDto event,
        @Valid @NotNull ConsequenceDto optionA,
        @Valid @NotNull ConsequenceDto optionB,
        @NotBlank @Size(max = 400) String playerInput
) {
}