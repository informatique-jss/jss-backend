package com.jss.osiris.libs;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule.Feature;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @SuppressWarnings({ "all" })
    public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        for (HttpMessageConverter converter : converters) {
            if (converter instanceof org.springframework.http.converter.json.MappingJackson2HttpMessageConverter) {
                ObjectMapper mapper = ((MappingJackson2HttpMessageConverter) converter).getObjectMapper();
                Hibernate5JakartaModule module = new Hibernate5JakartaModule();
                module.enable(Feature.FORCE_LAZY_LOADING);
                module.disable(Hibernate5JakartaModule.Feature.USE_TRANSIENT_ANNOTATION);
                mapper.registerModule(module);
            }
        }
    }
}