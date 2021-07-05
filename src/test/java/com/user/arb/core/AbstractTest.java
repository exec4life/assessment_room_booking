package com.user.arb.core;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.user.arb.service.AbstractService;
import com.user.arb.service.dto.AbstractDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentHashMap;

@ActiveProfiles("test")
public abstract class AbstractTest {

    protected ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    public AbstractTest() {
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        mapper.configure(Feature.IGNORE_UNDEFINED, true);
    }

    protected <D extends AbstractDTO> List<D> fromJsonToList(String jsonFilePath, TypeReference<List<D>> type,
                                                             AbstractService service) {
        try {
            List<D> list = mapper.readValue(getClass().getClassLoader().getResource(jsonFilePath), type);

            list.forEach(e -> {
                D d = (D)service.create(e);
            });

            return list;
        } catch (JsonProcessingException | MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
