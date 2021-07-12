package com.user.arb.mock.init;

import com.user.arb.jpa.entity.Room;
import com.user.arb.jpa.entity.Schedule;
import com.user.arb.jpa.entity.ScheduleDetail;
import com.user.arb.jpa.entity.User;
import com.user.arb.service.dto.ScheduleDTO;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScheduleServiceMockInit {

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static ScheduleDetail createScheduleDetail(Schedule schedule, String startTime, String endTime) {
        ScheduleDetail scheduleDetail = new ScheduleDetail();
        scheduleDetail.setStartTime(LocalDateTime.parse(startTime, formatter));
        scheduleDetail.setEndTime(LocalDateTime.parse(endTime, formatter));
        scheduleDetail.setSchedule(schedule);
        return scheduleDetail;
    }

    public static Schedule createSchedule(String startTime, String endTime) {
        Schedule schedule = new Schedule();
        schedule.setSubject("Subject");
        schedule.setStartTime(LocalDateTime.parse(startTime, formatter));
        schedule.setEndTime(LocalDateTime.parse(endTime, formatter));

        Room room = new Room();
        room.setId(1l);
        schedule.setRoom(room);

        User user = new User();
        user.setId(1l);
        schedule.setUser(user);

        return schedule;
    }

    public static ScheduleDTO createScheduleDTO(String startTime, String endTime) {
        ScheduleDTO schedule = new ScheduleDTO();
        schedule.setStartTime(LocalDateTime.parse(startTime, formatter));
        schedule.setEndTime(LocalDateTime.parse(endTime, formatter));
        schedule.setRoomId(1l);
        schedule.setUserId(1l);
        return schedule;
    }
}
