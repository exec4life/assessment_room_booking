package com.user.arb.service.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class UserDTO extends AbstractDTO {

    private String username;

    @JsonProperty("Username")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
