package com.user.arb.service;

import com.user.arb.controller.request.ScheduleSearchRequest;
import com.user.arb.jpa.entity.Schedule;
import com.user.arb.service.dto.ScheduleDTO;

import java.util.List;

public interface ScheduleService extends AbstractService<Schedule, ScheduleDTO, Long> {

    List<ScheduleDTO> findByRoom(Long roomId);

    List<ScheduleDTO> findByRoomIdIn(List<Long> ids);

    List<ScheduleDTO> search(ScheduleSearchRequest requestBody);
}
