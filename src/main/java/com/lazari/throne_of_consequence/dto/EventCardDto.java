package com.lazari.throne_of_consequence.dto;

import jakarta.validation.constraints.NotBlank;

public record EventCardDto(
        @NotBlank String id,
        @NotBlank String title,
        @NotBlank String description,
        String imagePath
) {
}