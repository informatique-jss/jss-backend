package com.jss.osiris.modules.miscellaneous.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module.Feature;
import com.jss.osiris.libs.JacksonLocalDateDeserializer;
import com.jss.osiris.libs.JacksonLocalDateSerializer;
import com.jss.osiris.libs.JacksonLocalDateTimeDeserializer;
import com.jss.osiris.libs.JacksonLocalDateTimeSerializer;
import com.jss.osiris.libs.exception.OsirisException;
import com.jss.osiris.modules.miscellaneous.model.Constant;
import com.jss.osiris.modules.miscellaneous.repository.ConstantRepository;

@Service
public class ConstantServiceProxyImpl {

    @Autowired
    ConstantRepository constantRepository;

    @Cacheable(value = "constant", key = "#root.methodName")
    public Constant getConstants() throws OsirisException {
        List<Constant> constants = IterableUtils.toList(constantRepository.findAll());
        if (constants == null || constants.size() != 1)
            throw new OsirisException(null, "Constants not defined or multiple");
        fecthAllProperty(constants.get(0));
        return constants.get(0);
    }

    @Caching(evict = {
            @CacheEvict(value = "constant", allEntries = true),
    })
    public Constant addOrUpdateConstant(
            Constant constant) throws OsirisException {
        constantRepository.save(constant);
        return getConstants();
    }

    private void fecthAllProperty(Constant contants) {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule("SimpleModule");
        simpleModule.addSerializer(LocalDateTime.class, new JacksonLocalDateTimeSerializer());
        simpleModule.addSerializer(LocalDate.class, new JacksonLocalDateSerializer());
        simpleModule.addDeserializer(LocalDateTime.class, new JacksonLocalDateTimeDeserializer());
        simpleModule.addDeserializer(LocalDate.class, new JacksonLocalDateDeserializer());
        objectMapper.registerModule(simpleModule);
        Hibernate5Module module = new Hibernate5Module();
        module.enable(Feature.FORCE_LAZY_LOADING);
        objectMapper.registerModule(module);
        try {
            objectMapper.writeValueAsString(contants);
        } catch (JsonProcessingException e) {
        }
    }
}
