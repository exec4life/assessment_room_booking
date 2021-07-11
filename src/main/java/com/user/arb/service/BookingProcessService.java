package com.user.arb.service;

import com.user.arb.service.behavior.BookingBehavior;
import com.user.arb.service.dto.BookingDTO;
import com.user.arb.service.generic.CoupleGRO;

import java.util.Map;

public interface BookingProcessService {
    CoupleGRO<BookingDTO, BookingBehavior> serializeRequestBody(Map<String, Object> requestBody);
}
