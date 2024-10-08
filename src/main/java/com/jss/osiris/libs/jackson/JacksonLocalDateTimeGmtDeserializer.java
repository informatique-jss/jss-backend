package com.jss.osiris.libs.jackson;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JacksonLocalDateTimeGmtDeserializer extends StdDeserializer<LocalDateTime> {

    private static final long serialVersionUID = 1355852411036457107L;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");

    public JacksonLocalDateTimeGmtDeserializer() {
        this(null);
    }

    protected JacksonLocalDateTimeGmtDeserializer(Class<LocalDateTime> type) {
        super(type);
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String date = p.getText();
        return LocalDateTime.parse(date, formatter);
    }

}