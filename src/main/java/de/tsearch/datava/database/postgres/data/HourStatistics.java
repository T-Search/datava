package de.tsearch.datava.database.postgres.data;

import lombok.Getter;
import lombok.ToString;

public record HourStatistics(Integer hour, long count) implements ChartStatistics{
    @Override
    public String getLabel() {
        return hour.toString();
    }

    @Override
    public long getCount() {
        return count;
    }
}
