package org.example;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class StableClock extends Clock {
    private Instant instant = Instant.EPOCH;

    public void setInstant(Instant instant) {
        this.instant = instant;
    }

    @Override
    public ZoneId getZone() {
        return null;
    }

    @Override
    public Clock withZone(ZoneId zone) {
        return null;
    }

    @Override
    public Instant instant() {
        return instant;
    }
}
