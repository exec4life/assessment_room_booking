package com.user.arb.jpa.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Entity
@Table(name = "tbl_booking_detail")
public class BookingDetail extends AbstractEntity implements Serializable, Cloneable {

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    private Booking booking;

    public BookingDetail() {
    }

    public BookingDetail(LocalDateTime startTime, LocalDateTime endTime, Booking booking) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.booking = booking;
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

    public Booking getBooking() {
        return booking;
    }

    public void setBooking(Booking booking) {
        this.booking = booking;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Booking)) return false;
        Booking booking = (Booking) obj;
        return Objects.equals(getStartTime(), booking.getStartTime())
                && Objects.equals(getEndTime(), booking.getEndTime())
                ;
    }

    @Override
    public Booking clone() throws CloneNotSupportedException {
        return (Booking) super.clone();
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
