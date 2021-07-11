package com.user.arb.service.impl;

import com.user.arb.config.ArbConfiguration;
import com.user.arb.controller.request.BookingSearchRequest;
import com.user.arb.exception.ArbException;
import com.user.arb.jpa.entity.Booking;
import com.user.arb.jpa.entity.BookingDetail;
import com.user.arb.jpa.entity.Room;
import com.user.arb.jpa.entity.User;
import com.user.arb.jpa.repository.BookingDetailRepository;
import com.user.arb.jpa.repository.BookingRepository;
import com.user.arb.jpa.repository.RoomRepository;
import com.user.arb.jpa.repository.UserRepository;
import com.user.arb.service.BookingService;
import com.user.arb.service.dto.BookingDTO;
import org.apache.commons.lang3.StringUtils;
import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityExistsException;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl extends AbstractServiceImpl<Booking, BookingDTO, Long> implements BookingService {

    public static final int MAX_INTERVAL = 365;
    private BookingRepository bookingRepository;
    private BookingDetailRepository bookingDetailRepository;
    private RoomRepository roomRepository;
    private UserRepository userRepository;
    private ArbConfiguration configuration;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository,
                              BookingDetailRepository bookingDetailRepository,
                              RoomRepository roomRepository,
                              UserRepository userRepository,
                              ArbConfiguration configuration) {
        super(bookingRepository);
        this.bookingRepository = bookingRepository;
        this.bookingDetailRepository = bookingDetailRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.configuration = configuration;
    }

    @Override
    protected BookingDTO toDtoConvert(Booking booking) {
        BookingDTO bookingDTO = super.toDtoConvert(booking);

        bookingDTO.setColor(booking.getRoom().getColor());
        bookingDTO.setRoomId(booking.getRoom().getId());
        bookingDTO.setUserId(booking.getUser().getId());

        return bookingDTO;
    }

    @Override
    protected Booking toEntityConvert(BookingDTO bookingDTO) {
        Booking booking = super.toEntityConvert(bookingDTO);

        Optional<Room> optionalRoom = roomRepository.findById(bookingDTO.getRoomId());
        Optional<User> optionalUser = userRepository.findById(bookingDTO.getUserId());

        if (!optionalRoom.isPresent()) {
            throw throwNonExistEntity(Room.class, bookingDTO.getRoomId());
        }

        if (!optionalUser.isPresent()) {
            throw throwNonExistEntity(User.class, bookingDTO.getUserId());
        }

        booking.setRoom(optionalRoom.get());
        booking.setUser(optionalUser.get());

        return booking;
    }

    @Override
    protected void toEntityCopy(BookingDTO bookingDTO, Booking booking) {
        super.toEntityCopy(bookingDTO, booking);

        Optional<Room> optionalRoom = roomRepository.findById(bookingDTO.getRoomId());
        Optional<User> optionalUser = userRepository.findById(bookingDTO.getUserId());

        if (!optionalRoom.isPresent()) {
            throw throwNonExistEntity(Room.class, bookingDTO.getRoomId());
        }

        if (!optionalUser.isPresent()) {
            throw throwNonExistEntity(User.class, bookingDTO.getUserId());
        }

        booking.setRoom(optionalRoom.get());
        booking.setUser(optionalUser.get());
    }

    @Override
    @Transactional
    public List<BookingDTO> findByRoom(Long roomId) {
        return bookingRepository.findByRoom(roomId).stream()
                .map(e -> toDtoConvert(e)).collect(Collectors.toList());
    }

    @Override
    public List<BookingDTO> findByRoomIdIn(List<Long> ids) {
        return bookingRepository.findByRoomIdIn(ids).stream()
                .map(e -> toDtoConvert(e)).collect(Collectors.toList());
    }

    @Override
    @Transactional(
            rollbackFor = IllegalArgumentException.class,
            noRollbackFor = EntityExistsException.class,
            rollbackForClassName = "IllegalArgumentException",
            noRollbackForClassName = "EntityExistsException")
    public BookingDTO create(BookingDTO bookingDTO) {
        checkBookingTime(bookingDTO);

        Booking booking = toEntityConvert(bookingDTO);
        booking.setId(null);
        List<BookingDetail> newBookingDetails = generateBookingDetails(booking);
        booking.setBookingDetails(newBookingDetails);

        List<BookingDetail> existedBookingDetails = bookingDetailRepository.findByDateRangeOfRoom(
                booking.getRoom().getId(),
                newBookingDetails.get(0).getStartTime(),
                newBookingDetails.get(newBookingDetails.size() - 1).getEndTime()
        );

        checkConflictBooking(newBookingDetails, existedBookingDetails, bookingDTO.getTimeZone());
        return toDtoConvert(bookingRepository.save(booking));
    }

    @Override
    @Transactional(
            rollbackFor = IllegalArgumentException.class,
            noRollbackFor = EntityExistsException.class,
            rollbackForClassName = "IllegalArgumentException",
            noRollbackForClassName = "EntityNotFoundException")
    public BookingDTO update(BookingDTO bookingDTO) {
        checkBookingTime(bookingDTO);

        Optional<Booking> optionalBooking = bookingRepository.findById(bookingDTO.getId());
        if (!optionalBooking.isPresent()) {
            throw throwNonExistEntity(entityClass, bookingDTO.getId());
        }

        Booking booking = toEntityConvert(bookingDTO);
        List<BookingDetail> newBookingDetails = generateBookingDetails(booking);
        booking.setBookingDetails(newBookingDetails);

        List<BookingDetail> bookingDetails = bookingDetailRepository.findByDateRangeOfRoomAndNotInBooking(
                bookingDTO.getRoomId(),
                bookingDTO.getId(),
                newBookingDetails.get(0).getStartTime(),
                newBookingDetails.get(newBookingDetails.size() - 1).getEndTime()
        );

        checkConflictBooking(newBookingDetails, bookingDetails, bookingDTO.getTimeZone());
        return toDtoConvert(bookingRepository.save(booking));
    }

    @Override
    public List<BookingDTO> search(BookingSearchRequest requestBody) {
        Set<Booking> bookings = bookingRepository.search(
                requestBody.getUsername() + "%",
                requestBody.getRoomId(),
                requestBody.getStartTime(), requestBody.getEndTime());
        return bookings.stream().map(e -> toDtoConvert(e)).collect(Collectors.toList());
    }

    private List<BookingDetail> generateBookingDetails(Booking booking) {
        List<BookingDetail> bookingDetails = new ArrayList<>();
        if (StringUtils.isEmpty(booking.getRecurrenceRule())) {
            bookingDetails.add(new BookingDetail(booking.getStartTime(), booking.getEndTime(), booking));
        } else {
            try {
                RecurrenceRule startRecurrenceRule = new RecurrenceRule(booking.getRecurrenceRule());
                RecurrenceRule endRecurrenceRule = new RecurrenceRule(booking.getRecurrenceRule());

                LocalDateTime startTime = booking.getStartTime();
                LocalDateTime endTime = booking.getEndTime();

                DateTime startDateTime = new DateTime(
                        startTime.getYear(), startTime.getMonthValue() - 1, startTime.getDayOfMonth(),
                        startTime.getHour(), startTime.getMinute(), startTime.getSecond());
                DateTime endDateTime = new DateTime(
                        endTime.getYear(), endTime.getMonthValue() - 1, endTime.getDayOfMonth(),
                        endTime.getHour(), endTime.getMinute(), endTime.getSecond());

                RecurrenceRuleIterator startItr = startRecurrenceRule.iterator(startDateTime);
                RecurrenceRuleIterator endItr = endRecurrenceRule.iterator(endDateTime);

                int maxInterval = MAX_INTERVAL;
                if (configuration.getMaxScheduleInterval() != null || configuration.getMaxScheduleInterval() > 0) {
                    maxInterval = configuration.getMaxScheduleInterval();
                }

                int count = 0;
                while (startItr.hasNext() && endItr.hasNext()) {
                    bookingDetails.add(
                            new BookingDetail(
                                    LocalDateTime.ofInstant(Instant.ofEpochMilli(startItr.nextDateTime().getTimestamp()),
                                            ZoneOffset.UTC),
                                    LocalDateTime.ofInstant(Instant.ofEpochMilli(endItr.nextDateTime().getTimestamp()),
                                            ZoneOffset.UTC),
                                    booking
                            )
                    );
                    if (++count > maxInterval) {
                        break;
                    }
                }
            } catch (InvalidRecurrenceRuleException e) {
                throw new ArbException(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("booking.validation.recurrence.rule.error",
                                null,
                                "RecurrenceRule is incorrect",
                                LocaleContextHolder.getLocale())
                );
            }
        }
        return bookingDetails;
    }

    private void checkConflictBooking(final List<BookingDetail> newBookingDetails, final List<BookingDetail> existedBookingDetails, TimeZone timeZone) {
        if (newBookingDetails.size() > 0 && existedBookingDetails.size() > 0) {
            for (BookingDetail newDetail : newBookingDetails) {
                for (BookingDetail existedDetail : existedBookingDetails) {
                    // {[]} || {[}] || [{]}
                    // Case 1: new time range is a boundary of existed time range
                    if ((newDetail.getStartTime().plusSeconds(-1).isBefore(existedDetail.getStartTime())
                            && newDetail.getEndTime().plusMinutes(1).isAfter(existedDetail.getEndTime()))
                            // Case 2: new end time is in existed time range
                            || (newDetail.getEndTime().isAfter(existedDetail.getStartTime())
                            && newDetail.getEndTime().plusMinutes(-1).isBefore(existedDetail.getEndTime()))
                            // Case 3: new start time is in existed time range
                            || (newDetail.getStartTime().plusMinutes(1).isAfter(existedDetail.getStartTime())
                            && newDetail.getStartTime().isBefore(existedDetail.getEndTime()))) {
                        Booking errorBooking = existedDetail.getBooking();

                        throw new ArbException(HttpStatus.BAD_REQUEST,
                                messageSource.getMessage("booking.validation.mixed",
                                        new Object[]{
                                                errorBooking.getSubject(),
                                                getLocalTime(errorBooking.getStartTime(), timeZone),
                                                getLocalTime(errorBooking.getEndTime(), timeZone)
                                        },
                                        "Schedule `{0}` [{1} - {2}] has picked this range",
                                        LocaleContextHolder.getLocale()));
                    }
                }
            }
        }
    }

    private LocalTime getLocalTime(LocalDateTime time, TimeZone timeZone) {
        ZonedDateTime zonedUTC = time.atZone(ZoneOffset.UTC);
        ZonedDateTime zonedIST = zonedUTC.withZoneSameInstant(timeZone.toZoneId());
        return zonedIST.toLocalTime();
    }

    private void checkBookingTime(BookingDTO bookingDTO) {
        if (bookingDTO.getStartTime().plusMinutes(15).isAfter(bookingDTO.getEndTime())) {
            throw new ArbException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("booking.validation.time.range",
                            null,
                            "The start time must be less than end time at less 15 minutes",
                            LocaleContextHolder.getLocale())
            );
        }
    }
}