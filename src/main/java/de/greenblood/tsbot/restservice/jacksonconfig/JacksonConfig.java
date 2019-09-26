package de.greenblood.tsbot.restservice.jacksonconfig;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import 
 org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import
 org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

//@Configuration
public class JacksonConfig extends WebMvcConfigurerAdapter {

    //@Bean
    //@Primary
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        setup(objectMapper);
        return objectMapper;
    }

    public void setup(ObjectMapper objectMapper) {

        objectMapper.setVisibility(PropertyAccessor.ALL,                    
                            JsonAutoDetect.Visibility.NONE);
        objectMapper.setVisibility(PropertyAccessor.GETTER, 
                            JsonAutoDetect.Visibility.PUBLIC_ONLY);

    }

    @Override
    public void configureMessageConverters(
                  List<HttpMessageConverter<?>> converters) {
        final MappingJackson2HttpMessageConverter converter = 
              getMappingJackson2HttpMessageConverter();

        converters.add(converter);
        super.configureMessageConverters(converters);
    }

    @Bean
    @Primary
    public MappingJackson2HttpMessageConverter
 getMappingJackson2HttpMessageConverter() {
        final MappingJackson2HttpMessageConverter converter = new 
                  MappingJackson2HttpMessageConverter();
        final ObjectMapper objectMapper = new ObjectMapper();

        setup(objectMapper);

        converter.setObjectMapper(objectMapper);
        converter.setPrettyPrint(true);
        return converter;
    }
}