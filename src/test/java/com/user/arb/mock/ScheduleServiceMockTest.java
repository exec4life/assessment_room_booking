package com.user.arb.mock;

import com.user.arb.config.ArbConfiguration;
import com.user.arb.core.AbstarctMockTest;
import com.user.arb.exception.ArbException;
import com.user.arb.jpa.entity.Schedule;
import com.user.arb.jpa.entity.ScheduleDetail;
import com.user.arb.jpa.repository.RoomRepository;
import com.user.arb.jpa.repository.ScheduleDetailRepository;
import com.user.arb.jpa.repository.ScheduleRepository;
import com.user.arb.jpa.repository.UserRepository;
import com.user.arb.mock.init.ScheduleServiceMockInit;
import com.user.arb.service.dto.ScheduleDTO;
import com.user.arb.service.impl.ScheduleServiceImpl;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.MessageSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleServiceMockTest extends AbstarctMockTest {

    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private ScheduleDetailRepository scheduleDetailRepository;
    @Mock
    private RoomRepository roomRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ArbConfiguration configuration;
    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private ScheduleServiceImpl scheduleService;

    static Schedule expSchedule;
    static List<ScheduleDetail> scheduleDetails;
    static ScheduleDTO expectedDTO;

    @BeforeClass
    public static void init() {
        expSchedule = ScheduleServiceMockInit.createSchedule(
                "2021-07-12T02:00:00.000Z",
                "2021-07-12T03:00:00.000Z");
        scheduleDetails = new ArrayList<>();
        scheduleDetails.add(ScheduleServiceMockInit.createScheduleDetail(expSchedule,
                "2021-07-12T02:00:00.000Z",
                "2021-07-12T03:00:00.000Z"));
        expSchedule.setBookingDetails(scheduleDetails);

        expectedDTO = ScheduleServiceMockInit.createScheduleDTO(
                "2021-07-12T02:00:00.000Z",
                "2021-07-12T03:00:00.000Z");
    }

    @Test
    public void create_successful_whenTimeRangeIsCorrect() {
        List<ScheduleDetail> otherScheduleDetails = new ArrayList<>();
        otherScheduleDetails.add(ScheduleServiceMockInit.createScheduleDetail(expSchedule,
                "2021-07-12T03:00:00.000Z",
                "2021-07-12T04:00:00.000Z"));

        Mockito.when(scheduleDetailRepository.findByDateRangeOfRoom(1l,
                scheduleDetails.get(0).getStartTime(), scheduleDetails.get(0).getEndTime()))
                .thenReturn(otherScheduleDetails);
        Mockito.when(scheduleRepository.save(any(Schedule.class))).thenReturn(expSchedule);
        Mockito.when(roomRepository.findById(1l)).thenReturn(Optional.of(expSchedule.getRoom()));
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(expSchedule.getUser()));

        ScheduleDTO actualDTO = scheduleService.create(expectedDTO);
        Assert.assertEquals("Create new Schedule must be correct date time",
                expectedDTO.getStartTime(), actualDTO.getStartTime());
    }

    @Test
    public void create_unsuccessful_whenEndTimeExistedRange() {
        List<ScheduleDetail> otherScheduleDetails = new ArrayList<>();
        otherScheduleDetails.add(ScheduleServiceMockInit.createScheduleDetail(expSchedule,
                "2021-07-12T02:30:00.000Z",
                "2021-07-12T03:30:00.000Z"));

        Mockito.when(scheduleDetailRepository.findByDateRangeOfRoom(1l,
                scheduleDetails.get(0).getStartTime(), scheduleDetails.get(0).getEndTime()))
                .thenReturn(otherScheduleDetails);
        Mockito.when(roomRepository.findById(1l)).thenReturn(Optional.of(expSchedule.getRoom()));
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(expSchedule.getUser()));

        Exception ex = new Exception();
        try {
            ScheduleDTO actualDTO = scheduleService.create(expectedDTO);
        } catch (Exception e) {
            ex = e;
        }

        Assert.assertEquals("Create new Schedule with end time in existed range must have exception",
                ArbException.class, ex.getClass());
    }

    @Test
    public void create_unsuccessful_whenTimeInExistedRange() {
        List<ScheduleDetail> otherScheduleDetails = new ArrayList<>();
        otherScheduleDetails.add(ScheduleServiceMockInit.createScheduleDetail(expSchedule,
                "2021-07-12T02:10:00.000Z",
                "2021-07-12T03:30:00.000Z"));

        Mockito.when(scheduleDetailRepository.findByDateRangeOfRoom(1l,
                scheduleDetails.get(0).getStartTime(), scheduleDetails.get(0).getEndTime()))
                .thenReturn(otherScheduleDetails);
        Mockito.when(roomRepository.findById(1l)).thenReturn(Optional.of(expSchedule.getRoom()));
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(expSchedule.getUser()));

        Exception ex = new Exception();
        try {
            ScheduleDTO actualDTO = scheduleService.create(expectedDTO);
        } catch (Exception e) {
            ex = e;
        }

        Assert.assertEquals("Create new Schedule when time range in existed range must have exception",
                ArbException.class, ex.getClass());
    }

    @Test
    public void create_unsuccessful_whenEndTimeInExistedRange() {
        List<ScheduleDetail> otherScheduleDetails = new ArrayList<>();
        otherScheduleDetails.add(ScheduleServiceMockInit.createScheduleDetail(expSchedule,
                "2021-07-12T02:30:00.000Z",
                "2021-07-12T03:30:00.000Z"));

        Mockito.when(scheduleDetailRepository.findByDateRangeOfRoom(1l,
                scheduleDetails.get(0).getStartTime(), scheduleDetails.get(0).getEndTime()))
                .thenReturn(otherScheduleDetails);
        Mockito.when(roomRepository.findById(1l)).thenReturn(Optional.of(expSchedule.getRoom()));
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(expSchedule.getUser()));

        Exception ex = new Exception();
        try {
            ScheduleDTO actualDTO = scheduleService.create(expectedDTO);
        } catch (Exception e) {
            ex = e;
        }

        Assert.assertEquals("Create new Schedule when end time in existed range must have exception",
                ArbException.class, ex.getClass());
    }

    @Test
    public void create_unsuccessful_whenTimeReangeMixTheExistedRange() {
        List<ScheduleDetail> otherScheduleDetails = new ArrayList<>();
        otherScheduleDetails.add(ScheduleServiceMockInit.createScheduleDetail(expSchedule,
                "2021-07-12T02:00:00.000Z",
                "2021-07-12T03:00:00.000Z"));

        Mockito.when(scheduleDetailRepository.findByDateRangeOfRoom(1l,
                scheduleDetails.get(0).getStartTime(), scheduleDetails.get(0).getEndTime()))
                .thenReturn(otherScheduleDetails);
        Mockito.when(roomRepository.findById(1l)).thenReturn(Optional.of(expSchedule.getRoom()));
        Mockito.when(userRepository.findById(1l)).thenReturn(Optional.of(expSchedule.getUser()));

        Exception ex = new Exception();
        try {
            ScheduleDTO actualDTO = scheduleService.create(expectedDTO);
        } catch (Exception e) {
            ex = e;
        }

        Assert.assertEquals("Create new Schedule when time range mix the existed range must have exception",
                ArbException.class, ex.getClass());
    }

}
