package de.tsearch.datava.database.postgres.entity;

import lombok.ToString;

@ToString
public class HourStatistics {
    private Integer hours;
    private Long count;

    public HourStatistics(Integer hour, Long count) {
        this.hours = hour;
        this.count = count;
    }
}
