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

public class BookingDTO extends AbstractDTO {

    @Size(min = 5, max = 50, message = "{booking.validation.subject.size}")
    private String subject;

    @Size(min = 5, max = 50, message = "{booking.validation.subject.size}")
    private String description;

    @NotNull(message = "{booking.validation.start.time.null}")
    private LocalDateTime startTime;

    @NotNull(message = "{booking.validation.end.time.null}")
    private LocalDateTime endTime;

    private boolean isAllDay;
    private boolean isReadonly;
    private String recurrenceRule;

    @NotNull(message = "{booking.validation.room.null}")
    private Long RoomId;

    @NotNull(message = "{booking.validation.user.null}")
    private Long UserId;

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

    @JsonProperty("StartTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="UTC")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime getStartTime() {
        return startTime;
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    @JsonProperty("EndTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="UTC")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @JsonSerialize(using = LocalDateTimeSerializer.class)
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
}
