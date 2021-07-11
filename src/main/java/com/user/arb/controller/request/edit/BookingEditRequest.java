package com.user.arb.controller.request.edit;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

public class BookingEditRequest {

    @NotNull(message = "{booking.validation.room.null}")
    private Long RoomId;

    @NotNull(message = "{booking.validation.user.null}")
    private Long UserId;

    private BookingEditRequestAdded[] added;

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

    public BookingEditRequestAdded[] getAdded() {
        return added;
    }

    public void setAdded(BookingEditRequestAdded[] added) {
        this.added = added;
    }
}


