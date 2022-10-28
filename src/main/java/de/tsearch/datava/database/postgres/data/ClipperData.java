package de.tsearch.datava.database.postgres.data;

public record ClipperData(String name, long count) implements ChartStatistics {
    @Override
    public String getLabel() {
        return name;
    }

    @Override
    public long getCount() {
        return count;
    }
}
