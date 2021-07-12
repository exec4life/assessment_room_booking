package com.user.arb.jpa.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "tbl_schedule")
public class Schedule extends AbstractEntity implements Serializable, Cloneable {

    @Column(nullable = false, length = 50)
    private String subject;

    private String description;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private boolean isAllDay;

    private boolean isReadonly;

    private String recurrenceRule;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToMany(mappedBy = "schedule", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ScheduleDetail> scheduleDetails = new ArrayList<>();

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

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

    public boolean isAllDay() {
        return isAllDay;
    }

    public void setAllDay(boolean allDay) {
        isAllDay = allDay;
    }

    public boolean isReadonly() {
        return isReadonly;
    }

    public void setReadonly(boolean readonly) {
        isReadonly = readonly;
    }

    public String getRecurrenceRule() {
        return recurrenceRule;
    }

    public void setRecurrenceRule(String recurrenceRule) {
        this.recurrenceRule = recurrenceRule;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ScheduleDetail> getBookingDetails() {
        return scheduleDetails;
    }

    public void setBookingDetails(List<ScheduleDetail> scheduleDetails) {
        this.scheduleDetails = scheduleDetails;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Schedule)) return false;
        Schedule schedule = (Schedule) obj;
        return Objects.equals(getSubject(), schedule.getSubject())
                && Objects.equals(getRoom(), schedule.getRoom())
                && Objects.equals(getStartTime(), schedule.getStartTime())
                && Objects.equals(getEndTime(), schedule.getEndTime())
                ;
    }

    @Override
    public Schedule clone() throws CloneNotSupportedException {
        return (Schedule) super.clone();
    }

    @Override
    public String toString() {
        return String.join("#", new String[]{
                getId().toString(), getSubject(), getActive().toString()
        });
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
