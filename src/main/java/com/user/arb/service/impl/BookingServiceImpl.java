package com.user.arb.service.impl;

import com.user.arb.jpa.entity.Booking;
import com.user.arb.jpa.repository.BookingRepository;
import com.user.arb.service.BookingService;
import com.user.arb.service.dto.BookingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingServiceImpl extends AbstractServiceImpl<Booking, BookingDTO, Long> implements BookingService {

    private BookingRepository bookingRepository;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository) {
        super(bookingRepository);
        this.bookingRepository = bookingRepository;
    }
}