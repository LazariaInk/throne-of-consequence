package com.lazari.throne_of_consequence.service;

import com.lazari.throne_of_consequence.dto.ConsequenceDto;
import com.lazari.throne_of_consequence.dto.EventCardDto;
import org.springframework.stereotype.Component;

@Component
public class PromptFactory {

    public String buildSystemPrompt() {
        return """
            You are a strict classifier for a medieval kingdom decision game.

            Your job is NOT to invent a new decision.
            Your job is ONLY to classify the player's message as:
            - A
            - B
            - C

            Return ONLY valid JSON.
            No markdown.
            No explanations outside JSON.

            Valid output format:
            {"decision":"A","narrative":"...","reason":"..."}
            or
            {"decision":"B","narrative":"...","reason":"..."}
            or
            {"decision":"C","narrative":"...","reason":"..."}

            Classification rules:
            1. Choose A only if the player clearly supports option A.
            2. Choose B only if the player clearly supports option B.
            3. Choose C if the message is unclear, ambiguous, contradictory, absurd, random, off-topic, trolling, modern-unrelated, or outside the context of the event.
            4. If unsure, choose C.
            5. Do not reward vague diplomacy unless it clearly matches A or B.
            6. narrative must be in Romanian, short, medieval in tone.
            7. reason must be in Romanian and short.
            8. Never output anything except the JSON object.
            """;
    }

    public String buildUserPrompt(
            EventCardDto event,
            ConsequenceDto optionA,
            ConsequenceDto optionB,
            ConsequenceDto optionC,
            String playerInput
    ) {
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

                OPTIUNEA C
                titlu: %s
                text: %s
                efecte: religion=%+d, population=%+d, army=%+d, money=%+d

                MESAJUL JUCATORULUI
                %s

                Alege strict intentia jucatorului:
                - A = sustine clar prima directie
                - B = sustine clar a doua directie
                - C = raspuns neclar, ambiguu, in afara contextului, absurd sau fara legatura

                Daca raspunsul nu este clar aliniat cu A sau B, alege C.
                """.formatted(
                safe(event.id()),
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

                safe(optionC.title()),
                safe(optionC.text()),
                optionC.religion(),
                optionC.population(),
                optionC.army(),
                optionC.money(),

                safe(playerInput)
        );
    }

    private String safe(String value) {
        return value == null ? "" : value.strip();
    }
}