package com.lazari.throne_of_consequence.exception;

public class OllamaIntegrationException extends RuntimeException {

    public OllamaIntegrationException(String message) {
        super(message);
    }

    public OllamaIntegrationException(String message, Throwable cause) {
        super(message, cause);
    }
}