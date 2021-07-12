package com.user.arb.service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.TimeZone;

public class ScheduleDTO extends AbstractDTO {

    @Size(min = 3, max = 50, message = "{schedule.validation.subject.size}")
    private String subject;

    @Size(min = 5, max = 50, message = "{schedule.validation.subject.size}")
    private String description;

    @NotNull(message = "{schedule.validation.start.time.empty}")
    @JsonProperty("StartTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="UTC")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime startTime;

    @NotNull(message = "{schedule.validation.end.time.empty}")
    @JsonProperty("EndTime")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone="UTC")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime endTime;

    private boolean isAllDay;
    private boolean isReadonly;
    private String recurrenceRule;
    private String color;

    @NotNull(message = "{schedule.validation.room.empty}")
    private Long RoomId;

    @NotNull(message = "{schedule.validation.user.empty}")
    private Long UserId;

    private TimeZone timeZone = TimeZone.getTimeZone(ZoneOffset.systemDefault());

    @Override
    @JsonProperty("Id")
    public Long getId() {
        return super.getId();
    }

    @JsonProperty("IsBlock")
    public Boolean getIsBlock() {
        return !super.getActive();
    }

    public void setIsBlock(Boolean active) {
        super.setActive(!active);
    }

    @JsonProperty("Subject")
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    @JsonProperty("Description")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    @JsonProperty("IsAllDay")
    public boolean isAllDay() {
        return isAllDay;
    }

    public void setAllDay(boolean allDay) {
        isAllDay = allDay;
    }

    @JsonProperty("IsReadonly")
    public boolean isReadonly() {
        return isReadonly;
    }

    public void setReadonly(boolean readonly) {
        isReadonly = readonly;
    }

    @JsonProperty("RecurrenceRule")
    public String getRecurrenceRule() {
        return recurrenceRule;
    }

    public void setRecurrenceRule(String recurrenceRule) {
        this.recurrenceRule = recurrenceRule;
    }

    @JsonProperty("RoomId")
    public Long getRoomId() {
        return RoomId;
    }

    public void setRoomId(Long roomId) {
        RoomId = roomId;
    }

    @JsonProperty("UserId")
    public Long getUserId() {
        return UserId;
    }

    public void setUserId(Long userId) {
        UserId = userId;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    @JsonProperty("Color")
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
