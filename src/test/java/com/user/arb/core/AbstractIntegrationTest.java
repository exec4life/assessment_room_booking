package com.user.arb.core;

import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.user.arb.service.AbstractService;
import com.user.arb.service.dto.AbstractDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.TimeZone;

@ActiveProfiles("test")
public abstract class AbstractIntegrationTest {

    protected ObjectMapper mapper = new ObjectMapper();

    @Autowired
    MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    public AbstractIntegrationTest() {
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        mapper.configure(Feature.IGNORE_UNDEFINED, true);
        mapper.setTimeZone(TimeZone.getDefault());
    }

    protected String readJsonFile(String jsonFilePath) {
        try {
            jsonFilePath = getClass().getClassLoader().getResource(jsonFilePath).getFile();
            return new String(Files.readAllBytes(Paths.get(jsonFilePath.substring(1))));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "{}";
    }

    protected <D extends AbstractDTO> List<D> fromJsonToList(String jsonFilePath, TypeReference<List<D>> type,
                                                             AbstractService service) {
        try {
            List<D> list = mapper.readValue(getClass().getClassLoader().getResource(jsonFilePath), type);

            list.forEach(e -> {
                D d = (D) service.create(e);
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
