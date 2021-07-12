package com.user.arb.service.behavior;

public enum ScheduleBehavior {

    INSERT("eventCreate"),
    UPDATE("eventChange"),
    DELETE("eventRemove");

    private String val;

    ScheduleBehavior(String val) {
        this.val = val;
    }

    public String getVal() {
        return val;
    }
}
