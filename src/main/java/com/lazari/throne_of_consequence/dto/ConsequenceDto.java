package com.lazari.throne_of_consequence.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotBlank;

public record ConsequenceDto(
        @NotBlank String title,
        @NotBlank String text,
        int religion,
        int population,
        int army,
        int money
) {
}