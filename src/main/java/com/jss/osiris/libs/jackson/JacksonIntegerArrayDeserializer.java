package com.jss.osiris.libs.jackson;

import java.io.IOException;
import java.util.Arrays;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class JacksonIntegerArrayDeserializer extends JsonDeserializer<Integer[]> {

    public JacksonIntegerArrayDeserializer() {
    }

    @Override
    public Integer[] deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        String value = p.getValueAsString();
        if (value == null || value.isEmpty()) {
            return new Integer[0]; // ou null selon votre besoin
        }
        String[] stringArray = value.split(",");
        return Arrays.stream(stringArray)
                .map(String::trim) // Supprimer les espaces autour
                .filter(s -> !s.isEmpty()) // Ignorer les chaînes vides
                .map(Integer::valueOf) // Convertir en Integer
                .toArray(Integer[]::new); // Convertir en tableau
    }
}