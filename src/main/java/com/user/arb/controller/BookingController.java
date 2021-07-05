package com.user.arb.controller;

import com.user.arb.controller.request.BookingSearchRequest;
import com.user.arb.service.BookingService;
import com.user.arb.service.dto.BookingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    private BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> find(@PathVariable("id") Long id) {
        return ResponseEntity.ok().body(bookingService.find(id));
    }

    @GetMapping(value = "/list/{roomId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> list(@PathVariable("roomId") Long roomId) {
        return ResponseEntity.ok().body(bookingService.findByRoom(roomId));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
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

}
