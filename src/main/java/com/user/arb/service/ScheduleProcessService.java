package com.user.arb.service;

import com.user.arb.service.behavior.ScheduleBehavior;
import com.user.arb.service.dto.ScheduleDTO;
import com.user.arb.service.generic.CoupleGRO;

import java.util.Map;

public interface ScheduleProcessService {
    CoupleGRO<ScheduleDTO, ScheduleBehavior> serializeRequestBody(Map<String, Object> requestBody);
}
