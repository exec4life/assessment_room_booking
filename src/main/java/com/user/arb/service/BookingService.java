package com.user.arb.service;

import com.user.arb.controller.request.BookingSearchRequest;
import com.user.arb.jpa.entity.Booking;
import com.user.arb.service.behavior.BookingBehavior;
import com.user.arb.service.dto.BookingDTO;
import com.user.arb.service.generic.CoupleGRO;
import com.user.arb.service.generic.TripleGRO;

import java.util.List;
import java.util.Map;

public interface BookingService extends AbstractService<Booking, BookingDTO, Long> {

    List<BookingDTO> findByRoom(Long roomId);

    List<BookingDTO> findByRoomIdIn(List<Long> ids);

    List<BookingDTO> search(BookingSearchRequest requestBody);
}
