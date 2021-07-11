package com.user.arb.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Size;

public class RoomDTO extends AbstractDTO {

    @Size(min = 1, max = 20, message = "{room.validation.name.size}")
    private String name;

    private String color;

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("Color")
    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
