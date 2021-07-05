package com.user.arb.service;

import com.user.arb.controller.request.BookingSearchRequest;
import com.user.arb.jpa.entity.Booking;
import com.user.arb.service.dto.BookingDTO;

import java.util.List;

public interface BookingService extends AbstractService<Booking, BookingDTO, Long> {

    List<BookingDTO> findByRoom(Long roomId);

    List<BookingDTO> search(BookingSearchRequest requestBody);
}
