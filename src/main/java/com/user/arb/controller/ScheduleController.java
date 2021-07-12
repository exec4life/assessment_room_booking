package com.user.arb.controller;

import com.user.arb.controller.request.ScheduleSearchRequest;
import com.user.arb.service.ScheduleProcessService;
import com.user.arb.service.ScheduleService;
import com.user.arb.service.behavior.ScheduleBehavior;
import com.user.arb.service.dto.ScheduleDTO;
import com.user.arb.service.generic.CoupleGRO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    private ScheduleService scheduleService;
    @Autowired
    @Qualifier("SyncfusionScheduleProcess")
    private ScheduleProcessService scheduleProcessService;

    @Autowired
    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> find(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(scheduleService.find(id));
    }

    @PostMapping(value = "/list/{roomId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list(@PathVariable("roomId") Long roomId) {
        return ResponseEntity.ok().body(scheduleService.findByRoom(roomId));
    }

    @PostMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listByRooms(@RequestBody Map<String, Object> requestBody) {
        if (requestBody.get("RoomIds") == null) {
            return ResponseEntity.ok().body(new Object[]{});
        }

        List<Long> roomIds = ((List<Integer>) requestBody.get("RoomIds")).stream()
                .mapToLong(Integer::longValue).boxed().collect(Collectors.toList());
        return ResponseEntity.ok().body(scheduleService.findByRoomIdIn(roomIds));
    }

    @PostMapping(value = "/syncfusion", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> syncfusionScheduleActions(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        CoupleGRO<ScheduleDTO, ScheduleBehavior> result = scheduleProcessService.serializeRequestBody(requestBody);
        ScheduleDTO dto = result.getFirstObject();
        dto.setTimeZone(getClientTimeZone(request));

        if (result.getSecondObject().equals(ScheduleBehavior.INSERT)) {
            dto = scheduleService.create(result.getFirstObject());
        } else if (result.getSecondObject().equals(ScheduleBehavior.UPDATE)) {
            dto = scheduleService.update(result.getFirstObject());
        } else if (result.getSecondObject().equals(ScheduleBehavior.DELETE)) {
            scheduleService.delete(result.getFirstObject().getId());
        }
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@Valid @RequestBody ScheduleDTO dto) {
        return ResponseEntity.ok().body(scheduleService.create(dto));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody ScheduleDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok().body(scheduleService.update(dto));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        scheduleService.delete(id);
        return ResponseEntity.ok().body("");
    }

    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> search(@Valid @RequestBody ScheduleSearchRequest requestBody) {
        return ResponseEntity.ok().body(scheduleService.search(requestBody));
    }

    private TimeZone getClientTimeZone(HttpServletRequest request) {
        Locale clientLocale = request.getLocale();
        Calendar calendar = Calendar.getInstance(clientLocale);
        return calendar.getTimeZone();
    }

}
