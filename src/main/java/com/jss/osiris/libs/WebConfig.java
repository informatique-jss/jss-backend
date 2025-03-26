package com.jss.osiris.libs;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule;
import com.fasterxml.jackson.datatype.hibernate5.jakarta.Hibernate5JakartaModule.Feature;
import com.jss.osiris.libs.jackson.JacksonPageSerializer;

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

    // I put the method in the @Configuration class, but not sure if its good
    @Bean
    public SimpleModule jacksonPageWithJsonViewModule() {
        SimpleModule module = new SimpleModule("jackson-page-with-jsonview", Version.unknownVersion());
        module.addSerializer(PageImpl.class, new JacksonPageSerializer());
        return module;
    }
}