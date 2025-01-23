package com.jss.osiris.libs.jackson;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JacksonLocalDateDeserializer extends StdDeserializer<LocalDate> {

    private static final long serialVersionUID = 1355852411036457107L;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public JacksonLocalDateDeserializer() {
        this(null);
    }

    protected JacksonLocalDateDeserializer(Class<LocalDateTime> type) {
        super(type);
    }

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String date = p.getText();
        return LocalDate.parse(date, formatter);
    }
}