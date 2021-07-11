package com.user.arb.service.behavior;

public enum BookingBehavior {

    INSERT("eventCreate"),
    UPDATE("eventChange"),
    DELETE("eventRemove");

    private String val;

    BookingBehavior(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
