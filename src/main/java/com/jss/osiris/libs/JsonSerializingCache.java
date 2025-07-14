package com.jss.osiris.libs;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.concurrent.Callable;

import org.springframework.cache.Cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule.Feature;
import com.jss.osiris.libs.jackson.JacksonLocalDateDeserializer;
import com.jss.osiris.libs.jackson.JacksonLocalDateSerializer;
import com.jss.osiris.libs.jackson.JacksonLocalDateTimeSerializer;
import com.jss.osiris.libs.jackson.JacksonTimestampMillisecondDeserializer;

public class JsonSerializingCache implements Cache {

    private Cache delegate;
    private ObjectMapper objectMapper = null;

    public JsonSerializingCache(Cache delegate) {
        this.delegate = delegate;
    }

    public ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            objectMapper = new ObjectMapper();
            SimpleModule simpleModule = new SimpleModule("SimpleModule");
            simpleModule.addSerializer(LocalDateTime.class, new JacksonLocalDateTimeSerializer());
            simpleModule.addSerializer(LocalDate.class, new JacksonLocalDateSerializer());
            simpleModule.addDeserializer(LocalDateTime.class, new JacksonTimestampMillisecondDeserializer());
            simpleModule.addDeserializer(LocalDate.class, new JacksonLocalDateDeserializer());
            objectMapper.registerModule(simpleModule);
            Hibernate5JakartaModule module = new Hibernate5JakartaModule();
            module.enable(Feature.FORCE_LAZY_LOADING);
            objectMapper.registerModule(module);
        }
        return objectMapper;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Object getNativeCache() {
        return delegate.getNativeCache();
    }

    @Override
    public ValueWrapper get(Object key) {

        ValueWrapper valueWrapper = delegate.get(key);
        if (valueWrapper == null)
            return null;

        Object val = valueWrapper.get();
        if (val == null)
            return null;

        return valueWrapper;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        ValueWrapper valueWrapper = get(key);
        if (valueWrapper == null)
            return null;
        Object val = valueWrapper.get();
        return type.cast(val);
    }

    @Override
    public void put(Object key, Object value) {
        try {
            String json = getObjectMapper().writeValueAsString(value);
            delegate.put(key, getObjectMapper().readValue(json, value.getClass()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON serialization error", e);
        }
    }

    @Override
    public void evict(Object key) {
        delegate.evict(key);
    }

    @Override
    public void clear() {
        delegate.clear();
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        // Essaie d'abord de récupérer dans le cache
        ValueWrapper cached = get(key);
        if (cached != null) {
            return (T) cached.get();
        }

        // Sinon charge via valueLoader et stocke en cache
        try {
            T value = valueLoader.call();
            put(key, value);
            return value;
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }
}
