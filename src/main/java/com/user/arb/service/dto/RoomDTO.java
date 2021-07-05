package com.user.arb.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.Size;

public class RoomDTO extends AbstractDTO {

    @Size(min = 1, max = 20, message = "{room.validation.name.size}")
    private String name;

    @JsonProperty("Name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
