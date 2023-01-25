package org.example.impl;

import org.example.EventsStatistic;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class EventStatisticImpl implements EventsStatistic {
    private final Clock clock;
    private final Map<String, List<Instant>> eventStatistic = new HashMap<>();

    public EventStatisticImpl(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void incEvent(String name) {
        List<Instant> instants = eventStatistic.get(name);
        if (instants != null) {
            instants.add(clock.instant());
            eventStatistic.put(name, instants);
        } else {
            eventStatistic.put(name, new ArrayList<>(Collections.singletonList(clock.instant())));
        }
    }

    @Override
    public double getEventStatisticByName(String name) {
        Instant now = clock.instant();
        Instant startInstance = now.minus(1, ChronoUnit.HOURS);
        long instancesLastHour = eventStatistic.getOrDefault(name, Collections.emptyList()).stream()
                .filter(instant -> instant.compareTo(startInstance) >= 0 && instant.compareTo(now) <= 0)
                .count();
        return instancesLastHour / 60d;
    }

    @Override
    public Map<String, Double> getAllEventStatistic() {
        Map<String, Double> result = new HashMap<>();
        for (String eventName : eventStatistic.keySet()) {
            double eventRpm = getEventStatisticByName(eventName);
            if (eventRpm > 0) {
                result.put(eventName, eventRpm);
            }
        }
        return result;
    }

    @Override
    public void printStatistic() {
        System.out.println("Events statistic:");
        for (Map.Entry<String, Double> entry : getAllEventStatistic().entrySet()) {
            System.out.println("Event: " + entry.getKey() + ", rpm: " + entry.getValue());
        }
    }
}
