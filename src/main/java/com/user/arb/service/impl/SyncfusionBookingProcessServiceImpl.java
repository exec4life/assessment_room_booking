package com.user.arb.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.user.arb.service.BookingProcessService;
import com.user.arb.service.behavior.BookingBehavior;
import com.user.arb.service.dto.BookingDTO;
import com.user.arb.service.generic.CoupleGRO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Qualifier("SyncfusionBookingProcess")
public class SyncfusionBookingProcessServiceImpl implements BookingProcessService {
    protected ObjectMapper objectMapper;

    @Autowired
    public SyncfusionBookingProcessServiceImpl(MappingJackson2HttpMessageConverter converter) {
        this.objectMapper = converter.getObjectMapper();
    }

    @Override
    public CoupleGRO<BookingDTO, BookingBehavior> serializeRequestBody(Map<String, Object> requestBody) {
        CoupleGRO<BookingDTO, BookingBehavior> result = new CoupleGRO<>();
        String requestType = String.valueOf(requestBody.get("RequestType"));

        Map<String, Object> mapData = new HashMap<>();
        if (requestType.equals(BookingBehavior.INSERT.getVal())) {
            mapData = (Map<String, Object>) ((List) requestBody.get("added")).get(0);
            result.setSecondObject(BookingBehavior.INSERT);
        } else if (requestType.equals(BookingBehavior.UPDATE.getVal())) {
            List data = (List) requestBody.get("added");
            if (CollectionUtils.isEmpty(data)) {
                mapData = (Map<String, Object>) ((List) requestBody.get("changed")).get(0);
            } else {
                mapData = (Map<String, Object>) ((List) requestBody.get("added")).get(0);
                mapData.put("Id", ((Map<String, Object>) ((List) requestBody.get("changed")).get(0)).get("Id"));
            }
            result.setSecondObject(BookingBehavior.UPDATE);
        } else {
            List data = (List) requestBody.get("changed");
            if (CollectionUtils.isEmpty(data)) {
                data = (List) requestBody.get("deleted");
            }
            mapData = (Map<String, Object>) (data).get(0);
            result.setSecondObject(BookingBehavior.DELETE);
        }

        BookingDTO dto = objectMapper.convertValue(mapData, BookingDTO.class);
        if (dto.getRecurrenceRule() != null) {
            //FREQ=DAILY;INTERVAL=1;UNTIL=20210904T213000Z;
            Map<String, String> ruleMap = Arrays.stream(dto.getRecurrenceRule().split(";"))
                    .map(s -> s.split("=")).collect(Collectors.toMap(e -> e[0], e -> e[1]));
            if (ruleMap.containsKey("UNTIL")) {
                ruleMap.put("UNTIL", ruleMap.get("UNTIL").substring(0, 15));
                String recurrenceRule = String.join(";", ruleMap.keySet()
                        .stream().map(k -> k + "=" + ruleMap.get(k)).collect(Collectors.toList()));
                dto.setRecurrenceRule(recurrenceRule);
            }
        }

        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<BookingDTO>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
        result.setFirstObject(dto);
        return result;
    }
}
