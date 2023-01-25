package org.example.impl;

import org.example.EventsStatistic;
import org.example.StableClock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventStatisticImplTest {
    private StableClock clock;
    private EventsStatistic eventsStatistic;

    @BeforeEach
    public void prepareEnvironment() {
        this.clock = new StableClock();
        this.eventsStatistic = new EventStatisticImpl(clock);
    }

    @Test
    void noInstanceTest() {
        assertTrue(eventsStatistic.getAllEventStatistic().isEmpty());
        assertEquals(0, eventsStatistic.getEventStatisticByName("name"));
    }

    @Test
    void singleInstanceTest() {
        String eventName = "name";
        double expectedRpm = 1 / 60d;
        Map<String, Double> expectedAllEventStatistic = Map.of(eventName, expectedRpm);

        eventsStatistic.incEvent(eventName);

        assertEquals(expectedRpm, eventsStatistic.getEventStatisticByName(eventName));
        assertEquals(expectedAllEventStatistic, eventsStatistic.getAllEventStatistic());

        clock.setInstant(clock.instant().plus(1, ChronoUnit.HOURS));
        assertEquals(expectedRpm, eventsStatistic.getEventStatisticByName(eventName));
        assertEquals(expectedAllEventStatistic, eventsStatistic.getAllEventStatistic());

        clock.setInstant(clock.instant().plus(1, ChronoUnit.HOURS));
        assertEquals(0, eventsStatistic.getEventStatisticByName(eventName));
        assertEquals(Collections.EMPTY_MAP, eventsStatistic.getAllEventStatistic());
    }

    @Test
    void singleInstanceMultipleEventsTest() {
        String eventName = "name";
        eventsStatistic.incEvent(eventName);
        clock.setInstant(clock.instant().plus(10, ChronoUnit.MINUTES));

        eventsStatistic.incEvent(eventName);
        clock.setInstant(clock.instant().plus(10, ChronoUnit.MINUTES));

        eventsStatistic.incEvent(eventName);

        double expectedRpm = 3 / 60d;
        assertEquals(expectedRpm, eventsStatistic.getEventStatisticByName(eventName));
        assertEquals(Map.of(eventName, expectedRpm), eventsStatistic.getAllEventStatistic());

        clock.setInstant(Instant.EPOCH.plus(70, ChronoUnit.MINUTES));
        expectedRpm = 2 / 60d;
        assertEquals(expectedRpm, eventsStatistic.getEventStatisticByName(eventName));
        assertEquals(Map.of(eventName, expectedRpm), eventsStatistic.getAllEventStatistic());
    }

    @Test
    void multipleInstancesTest() {
        String firstEventName = "name1";
        String secondEventName = "name2";
        String thirdEventName = "name3";

        eventsStatistic.incEvent(firstEventName);
        eventsStatistic.incEvent(secondEventName);
        eventsStatistic.incEvent(thirdEventName);

        clock.setInstant(clock.instant().plus(10, ChronoUnit.MINUTES));
        eventsStatistic.incEvent(firstEventName);
        eventsStatistic.incEvent(secondEventName);

        clock.setInstant(clock.instant().plus(10, ChronoUnit.MINUTES));
        eventsStatistic.incEvent(firstEventName);

        Map<String, Double> expectedStatistic = Map.of(firstEventName, 3 / 60d, secondEventName, 2 / 60d, thirdEventName, 1 / 60d);
        assertEquals(expectedStatistic, eventsStatistic.getAllEventStatistic());

        for (Map.Entry<String, Double> expectedEntry : expectedStatistic.entrySet()) {
            assertEquals(expectedEntry.getValue(), eventsStatistic.getEventStatisticByName(expectedEntry.getKey()));
        }
    }

}