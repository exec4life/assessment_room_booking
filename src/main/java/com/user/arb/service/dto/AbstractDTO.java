package com.user.arb.service.dto;

import java.io.Serializable;

public abstract class AbstractDTO implements Serializable {

    private Long id;

    private Boolean active = Boolean.FALSE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
