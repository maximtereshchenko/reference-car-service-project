package com.andersenlab.carservice.extension;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

public final class ManualClock extends Clock {

    private Instant timestamp = Instant.parse("2000-01-01T00:00:00.00Z");

    @Override
    public ZoneId getZone() {
        return ZoneOffset.UTC;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return this;
    }

    @Override
    public Instant instant() {
        return timestamp;
    }

    public void waitOneHour() {
        timestamp = timestamp.plus(1, ChronoUnit.HOURS);
    }
}
