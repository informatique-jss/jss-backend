package com.jss.osiris.libs.jackson;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class JacksonLocalDateSerializer extends StdSerializer<LocalDate> {

    private static final long serialVersionUID = 1355852411036457107L;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public JacksonLocalDateSerializer() {
        this(null);
    }

    protected JacksonLocalDateSerializer(Class<LocalDate> type) {
        super(type);
    }

    @Override
    public void serialize(LocalDate value, JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(formatter.format(value));
    }

}