package com.jss.osiris.libs.jackson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class EmptyStringAsEmptyListDeserializer extends JsonDeserializer<List<Integer>> {

    @Override
    public List<Integer> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        if (p.getValueAsString() != null && p.getValueAsString().isEmpty()) {
            return new ArrayList<>();
        }
        return ctxt.readValue(p, ctxt.getTypeFactory().constructCollectionType(List.class, Integer.class));
    }
}
