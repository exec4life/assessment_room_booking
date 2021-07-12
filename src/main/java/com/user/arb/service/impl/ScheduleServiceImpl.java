package com.user.arb.service.impl;

import com.user.arb.config.ArbConfiguration;
import com.user.arb.controller.request.ScheduleSearchRequest;
import com.user.arb.exception.ArbException;
import com.user.arb.jpa.entity.Schedule;
import com.user.arb.jpa.entity.ScheduleDetail;
import com.user.arb.jpa.entity.Room;
import com.user.arb.jpa.entity.User;
import com.user.arb.jpa.repository.ScheduleDetailRepository;
import com.user.arb.jpa.repository.ScheduleRepository;
import com.user.arb.jpa.repository.RoomRepository;
import com.user.arb.jpa.repository.UserRepository;
import com.user.arb.service.ScheduleService;
import com.user.arb.service.dto.ScheduleDTO;
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
public class ScheduleServiceImpl extends AbstractServiceImpl<Schedule, ScheduleDTO, Long> implements ScheduleService {

    public static final int MAX_INTERVAL = 365;
    private ScheduleRepository scheduleRepository;
    private ScheduleDetailRepository scheduleDetailRepository;
    private RoomRepository roomRepository;
    private UserRepository userRepository;
    private ArbConfiguration configuration;

    @Autowired
    public ScheduleServiceImpl(ScheduleRepository scheduleRepository,
                               ScheduleDetailRepository scheduleDetailRepository,
                               RoomRepository roomRepository,
                               UserRepository userRepository,
                               ArbConfiguration configuration) {
        super(scheduleRepository);
        this.scheduleRepository = scheduleRepository;
        this.scheduleDetailRepository = scheduleDetailRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.configuration = configuration;
    }

    @Override
    @Transactional
    protected ScheduleDTO toDtoConvert(Schedule schedule) {
        ScheduleDTO scheduleDTO = super.toDtoConvert(schedule);

        scheduleDTO.setColor(schedule.getRoom().getColor());
        scheduleDTO.setRoomId(schedule.getRoom().getId());
        scheduleDTO.setUserId(schedule.getUser().getId());

        return scheduleDTO;
    }

    @Override
    protected Schedule toEntityConvert(ScheduleDTO scheduleDTO) {
        Schedule schedule = super.toEntityConvert(scheduleDTO);

        Optional<Room> optionalRoom = roomRepository.findById(scheduleDTO.getRoomId());
        Optional<User> optionalUser = userRepository.findById(scheduleDTO.getUserId());

        if (!optionalRoom.isPresent()) {
            throw throwNonExistEntity(Room.class, scheduleDTO.getRoomId());
        }

        if (!optionalUser.isPresent()) {
            throw throwNonExistEntity(User.class, scheduleDTO.getUserId());
        }

        schedule.setRoom(optionalRoom.get());
        schedule.setUser(optionalUser.get());

        return schedule;
    }

    @Override
    protected void toEntityCopy(ScheduleDTO scheduleDTO, Schedule schedule) {
        super.toEntityCopy(scheduleDTO, schedule);

        Optional<Room> optionalRoom = roomRepository.findById(scheduleDTO.getRoomId());
        Optional<User> optionalUser = userRepository.findById(scheduleDTO.getUserId());

        if (!optionalRoom.isPresent()) {
            throw throwNonExistEntity(Room.class, scheduleDTO.getRoomId());
        }

        if (!optionalUser.isPresent()) {
            throw throwNonExistEntity(User.class, scheduleDTO.getUserId());
        }

        schedule.setRoom(optionalRoom.get());
        schedule.setUser(optionalUser.get());
    }

    @Override
    @Transactional
    public List<ScheduleDTO> findByRoom(Long roomId) {
        return scheduleRepository.findByRoom(roomId).stream()
                .map(e -> toDtoConvert(e)).collect(Collectors.toList());
    }

    @Override
    public List<ScheduleDTO> findByRoomIdIn(List<Long> ids) {
        return scheduleRepository.findByRoomIdIn(ids).stream()
                .map(e -> toDtoConvert(e)).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ScheduleDTO create(ScheduleDTO scheduleDTO) {
        checkBookingTime(scheduleDTO);

        Schedule schedule = toEntityConvert(scheduleDTO);
        schedule.setId(null);
        List<ScheduleDetail> newScheduleDetails = generateBookingDetails(schedule);
        schedule.setBookingDetails(newScheduleDetails);

        List<ScheduleDetail> existedScheduleDetails = scheduleDetailRepository.findByDateRangeOfRoom(
                schedule.getRoom().getId(),
                newScheduleDetails.get(0).getStartTime(),
                newScheduleDetails.get(newScheduleDetails.size() - 1).getEndTime()
        );

        checkConflictBooking(newScheduleDetails, existedScheduleDetails, scheduleDTO.getTimeZone());
        return toDtoConvert(scheduleRepository.save(schedule));
    }

    @Override
    @Transactional
    public ScheduleDTO update(ScheduleDTO scheduleDTO) {
        checkBookingTime(scheduleDTO);

        Optional<Schedule> optionalBooking = scheduleRepository.findById(scheduleDTO.getId());
        if (!optionalBooking.isPresent()) {
            throw throwNonExistEntity(entityClass, scheduleDTO.getId());
        }

        Schedule schedule = toEntityConvert(scheduleDTO);
        List<ScheduleDetail> newScheduleDetails = generateBookingDetails(schedule);
        schedule.setBookingDetails(newScheduleDetails);

        List<ScheduleDetail> scheduleDetails = scheduleDetailRepository.findByDateRangeOfRoomAndNotInSchedule(
                scheduleDTO.getRoomId(),
                scheduleDTO.getId(),
                newScheduleDetails.get(0).getStartTime(),
                newScheduleDetails.get(newScheduleDetails.size() - 1).getEndTime()
        );

        checkConflictBooking(newScheduleDetails, scheduleDetails, scheduleDTO.getTimeZone());
        return toDtoConvert(scheduleRepository.save(schedule));
    }

    @Override
    public List<ScheduleDTO> search(ScheduleSearchRequest requestBody) {
        Set<Schedule> schedules = scheduleRepository.search(
                requestBody.getUsername() + "%",
                requestBody.getRoomId(),
                requestBody.getStartTime(), requestBody.getEndTime());
        return schedules.stream().map(e -> toDtoConvert(e)).collect(Collectors.toList());
    }

    private List<ScheduleDetail> generateBookingDetails(Schedule schedule) {
        List<ScheduleDetail> scheduleDetails = new ArrayList<>();
        if (StringUtils.isEmpty(schedule.getRecurrenceRule())) {
            scheduleDetails.add(new ScheduleDetail(schedule.getStartTime(), schedule.getEndTime(), schedule));
        } else {
            try {
                RecurrenceRule startRecurrenceRule = new RecurrenceRule(schedule.getRecurrenceRule());
                RecurrenceRule endRecurrenceRule = new RecurrenceRule(schedule.getRecurrenceRule());

                LocalDateTime startTime = schedule.getStartTime();
                LocalDateTime endTime = schedule.getEndTime();

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
                    scheduleDetails.add(
                            new ScheduleDetail(
                                    LocalDateTime.ofInstant(Instant.ofEpochMilli(startItr.nextDateTime().getTimestamp()),
                                            ZoneOffset.UTC),
                                    LocalDateTime.ofInstant(Instant.ofEpochMilli(endItr.nextDateTime().getTimestamp()),
                                            ZoneOffset.UTC),
                                    schedule
                            )
                    );
                    if (++count > maxInterval) {
                        break;
                    }
                }
            } catch (InvalidRecurrenceRuleException e) {
                throw new ArbException(HttpStatus.BAD_REQUEST,
                        messageSource.getMessage("schedule.validation.recurrence.rule.error",
                                null,
                                "RecurrenceRule is incorrect",
                                LocaleContextHolder.getLocale())
                );
            }
        }
        return scheduleDetails;
    }

    private void checkConflictBooking(final List<ScheduleDetail> newScheduleDetails, final List<ScheduleDetail> existedScheduleDetails, TimeZone timeZone) {
        if (newScheduleDetails.size() > 0 && existedScheduleDetails.size() > 0) {
            for (ScheduleDetail newDetail : newScheduleDetails) {
                for (ScheduleDetail existedDetail : existedScheduleDetails) {
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
                        Schedule errorSchedule = existedDetail.getSchedule();

                        throw new ArbException(HttpStatus.BAD_REQUEST,
                                messageSource.getMessage("schedule.validation.mixed",
                                        new Object[]{
                                                errorSchedule.getSubject(),
                                                getLocalTime(errorSchedule.getStartTime(), timeZone),
                                                getLocalTime(errorSchedule.getEndTime(), timeZone)
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

    private void checkBookingTime(ScheduleDTO scheduleDTO) {
        if (scheduleDTO.getStartTime().plusMinutes(15).isAfter(scheduleDTO.getEndTime())) {
            throw new ArbException(HttpStatus.BAD_REQUEST,
                    messageSource.getMessage("schedule.validation.time.range",
                            null,
                            "The start time must be less than end time at less 15 minutes",
                            LocaleContextHolder.getLocale())
            );
        }
    }
}