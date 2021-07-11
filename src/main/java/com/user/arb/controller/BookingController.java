package com.user.arb.controller;

import com.user.arb.controller.request.BookingSearchRequest;
import com.user.arb.service.BookingProcessService;
import com.user.arb.service.BookingService;
import com.user.arb.service.behavior.BookingBehavior;
import com.user.arb.service.dto.BookingDTO;
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
@RequestMapping("/api/booking")
public class BookingController {

    private BookingService bookingService;
    @Autowired
    @Qualifier("SyncfusionBookingProcess")
    private BookingProcessService bookingProcessService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> find(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(bookingService.find(id));
    }

    @PostMapping(value = "/list/{roomId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list(@PathVariable("roomId") Long roomId) {
        return ResponseEntity.ok().body(bookingService.findByRoom(roomId));
    }

    @PostMapping(value = "/list", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> listByRooms(@RequestBody Map<String, Object> requestBody) {
        if (requestBody.get("RoomIds") == null) {
            return ResponseEntity.ok().body(new Object[]{});
        }

        List<Long> roomIds = ((List<Integer>) requestBody.get("RoomIds")).stream()
                .mapToLong(Integer::longValue).boxed().collect(Collectors.toList());
        return ResponseEntity.ok().body(bookingService.findByRoomIdIn(roomIds));
    }

    @PostMapping(value = "/syncfusion", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> syncfusionScheduleActions(@RequestBody Map<String, Object> requestBody, HttpServletRequest request) {
        CoupleGRO<BookingDTO, BookingBehavior> result = bookingProcessService.serializeRequestBody(requestBody);
        BookingDTO dto = result.getFirstObject();
        dto.setTimeZone(getClientTimeZone(request));

        if (result.getSecondObject().equals(BookingBehavior.INSERT)) {
            dto = bookingService.create(result.getFirstObject());
        } else if (result.getSecondObject().equals(BookingBehavior.UPDATE)) {
            dto = bookingService.update(result.getFirstObject());
        } else if (result.getSecondObject().equals(BookingBehavior.DELETE)) {
            bookingService.delete(result.getFirstObject().getId());
        }
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> create(@Valid @RequestBody BookingDTO dto) {
        return ResponseEntity.ok().body(bookingService.create(dto));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody BookingDTO dto) {
        dto.setId(id);
        return ResponseEntity.ok().body(bookingService.update(dto));
    }

    @DeleteMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        bookingService.delete(id);
        return ResponseEntity.ok().body("");
    }

    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> search(@Valid @RequestBody BookingSearchRequest requestBody) {
        return ResponseEntity.ok().body(bookingService.search(requestBody));
    }

    private TimeZone getClientTimeZone(HttpServletRequest request) {
        Locale clientLocale = request.getLocale();
        Calendar calendar = Calendar.getInstance(clientLocale);
        return calendar.getTimeZone();
    }

}
