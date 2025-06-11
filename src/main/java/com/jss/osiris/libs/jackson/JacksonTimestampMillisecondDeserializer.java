package com.jss.osiris.libs.jackson;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

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
        String value = p.getText();

        try {
            // for timestamp string
            long millis = Long.parseLong(value);
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault());
        } catch (NumberFormatException e) {
            try {
                // for date format ISO
                DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
                return LocalDateTime.parse(value, formatter);
            } catch (Exception ex) {
                throw new IOException("Unable to parse LocalDateTime from value: " + value, ex);
            }
        }
    }
}