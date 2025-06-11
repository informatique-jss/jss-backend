package com.jss.osiris.libs.jackson;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class JacksonTimestampMillisecondDeserializer extends StdDeserializer<LocalDateTime> {

    private static final long serialVersionUID = 1355852411036457107L;

    public JacksonTimestampMillisecondDeserializer() {
        this(null);
    }

    protected JacksonTimestampMillisecondDeserializer(Class<LocalDateTime> type) {
        super(type);
    }

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String date = p.getText();
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(Long.parseLong(date.substring(0,
                date.length() - 3))),
                TimeZone.getDefault().toZoneId());
    }
}