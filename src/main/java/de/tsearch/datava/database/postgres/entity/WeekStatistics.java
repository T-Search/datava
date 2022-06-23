package de.tsearch.datava.database.postgres.entity;

import lombok.ToString;

@ToString
public class WeekStatistics {
    private WeekdayEnum weekday;
    private Long count;

    public WeekStatistics(Integer weekdayNumber, Long count) {
        this.weekday = WeekdayEnum.values()[weekdayNumber];
        this.count = count;
    }

    public enum WeekdayEnum {
        INVALID, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
    }
}
