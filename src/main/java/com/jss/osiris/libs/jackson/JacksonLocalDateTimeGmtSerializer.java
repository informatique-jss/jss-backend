package com.jss.osiris.libs.jackson;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class JacksonLocalDateTimeGmtSerializer extends StdSerializer<LocalDateTime> {

    private static final long serialVersionUID = 1355852411036457107L;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX");

    public JacksonLocalDateTimeGmtSerializer() {
        this(null);
    }

    protected JacksonLocalDateTimeGmtSerializer(Class<LocalDateTime> type) {
        super(type);
    }

    @Override
    public void serialize(LocalDateTime value, JsonGenerator jsonGenerator,
            SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeString(formatter.format(value));
    }

}