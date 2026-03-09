package com.lazari.throne_of_consequence.service;

import com.lazari.throne_of_consequence.dto.ConsequenceDto;
import com.lazari.throne_of_consequence.dto.EventCardDto;
import org.springframework.stereotype.Component;

@Component
public class PromptFactory {

    public String buildSystemPrompt() {
        return """
            You are a strict classifier for a medieval kingdom decision game.

            You must return ONLY valid JSON.
            No markdown.
            No explanations outside JSON.

            Valid output format:
            {"decision":"A","narrative":"...","reason":"..."}
            or
            {"decision":"B","narrative":"...","reason":"..."}
            or
            {"decision":"NONE","narrative":"...","reason":"..."}

            Rules:
            1. Choose NONE for any off-topic, absurd, random, joking, trolling, modern unrelated or unclear input.
            2. If unsure, choose NONE.
            3. Only choose A if the player clearly supports option A.
            4. Only choose B if the player clearly supports option B.
            5. narrative must be in Romanian, short, medieval in tone.
            6. reason must be in Romanian and short.
            """;
    }

    public String buildUserPrompt(EventCardDto event, ConsequenceDto optionA, ConsequenceDto optionB, String playerInput) {
        return """
                Analizeaza raspunsul jucatorului pentru acest eveniment.

                EVENIMENT
                id: %s
                titlu: %s
                descriere: %s

                OPTIUNEA A
                titlu: %s
                text: %s
                efecte: religion=%+d, population=%+d, army=%+d, money=%+d

                OPTIUNEA B
                titlu: %s
                text: %s
                efecte: religion=%+d, population=%+d, army=%+d, money=%+d

                MESAJUL JUCATORULUI
                %s

                Alege strict intentia jucatorului:
                - A
                - B
                - NONE

                Daca raspunsul este in afara contextului, random, absurd sau fara legatura cu evenimentul, alege NONE.
                """.formatted(
                event.id(),
                safe(event.title()),
                safe(event.description()),
                safe(optionA.title()),
                safe(optionA.text()),
                optionA.religion(),
                optionA.population(),
                optionA.army(),
                optionA.money(),
                safe(optionB.title()),
                safe(optionB.text()),
                optionB.religion(),
                optionB.population(),
                optionB.army(),
                optionB.money(),
                safe(playerInput)
        );
    }

    private String safe(String value) {
        return value == null ? "" : value.strip();
    }
}