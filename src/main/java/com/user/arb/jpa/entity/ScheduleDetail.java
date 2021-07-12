package com.user.arb.jpa.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Table(name = "tbl_schedule_detail")
public class ScheduleDetail extends AbstractEntity implements Serializable, Cloneable {

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    public ScheduleDetail() {
    }

    public ScheduleDetail(LocalDateTime startTime, LocalDateTime endTime, Schedule schedule) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.schedule = schedule;
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

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) {
        this.schedule = schedule;
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
        return Objects.equals(getStartTime(), schedule.getStartTime())
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
                getId().toString(),
                getStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                getEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), getActive().toString()
        });
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
    }
}
