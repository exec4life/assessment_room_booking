package com.user.arb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "com.user.arb")
public class ArbConfiguration {

    private Integer maxScheduleInterval;

    public Integer getMaxScheduleInterval() {
        return maxScheduleInterval;
    }

    public void setMaxScheduleInterval(Integer maxScheduleInterval) {
        this.maxScheduleInterval = maxScheduleInterval;
    }
}
