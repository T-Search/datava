package de.tsearch.datava.database.postgres.entity;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class HourStatistics {
    private Integer hour;
    private Long count;

    public HourStatistics(Integer hour, Long count) {
        this.hour = hour;
        this.count = count;
    }
}
