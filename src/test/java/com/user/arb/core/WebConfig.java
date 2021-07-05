package com.user.arb.core;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.time.ZoneOffset;
import java.util.TimeZone;

//@Configuration
public class WebConfig {

//    @Bean
    public ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        mapper.configure(JsonParser.Feature.IGNORE_UNDEFINED, true);
        mapper.setTimeZone(TimeZone.getTimeZone(ZoneOffset.UTC));
        return mapper;
    }

//    @Bean
//    @Autowired
    public MappingJackson2HttpMessageConverter jsonMapper(ObjectMapper objectMapper) {
        return new MappingJackson2HttpMessageConverter(objectMapper);
    }

}
