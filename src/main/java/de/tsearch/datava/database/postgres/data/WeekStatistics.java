package de.tsearch.datava.database.postgres.data;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class WeekStatistics implements ChartStatistics{
    private WeekdayEnum weekday;
    private long count;

    public WeekStatistics(Integer weekdayNumber, Long count) {
        this.weekday = WeekdayEnum.values()[weekdayNumber];
        this.count = count;
    }

    @Override
    public String getLabel() {
        return weekday.name();
    }

    public enum WeekdayEnum {
        INVALID, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
    }
}
