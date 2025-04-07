package com.jss.osiris.libs.jackson;

import java.io.IOException;

import org.springframework.data.domain.PageImpl;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

public class JacksonPageSerializer extends StdSerializer<PageImpl> {

    public JacksonPageSerializer() {
        super(PageImpl.class);
    }

    @Override
    public void serialize(PageImpl value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();

        // We create the page attribute to encapsulate all the pagination fields
        gen.writeFieldName("page");
        gen.writeStartObject();
        gen.writeNumberField("pageNumber", value.getNumber());
        gen.writeNumberField("numberOfElements", value.getNumberOfElements());
        gen.writeNumberField("totalElements", value.getTotalElements());
        gen.writeNumberField("totalPages", value.getTotalPages());
        gen.writeNumberField("pageSize", value.getSize());
        gen.writeEndObject();

        // We fill the second attribute with the content
        gen.writeFieldName("content");
        provider.defaultSerializeValue(value.getContent(), gen);

        gen.writeEndObject();
    }
}
