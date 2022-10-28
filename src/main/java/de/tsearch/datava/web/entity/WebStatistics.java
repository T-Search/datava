package de.tsearch.datava.web.entity;

import de.tsearch.datava.database.postgres.data.BoxStatistics;
import de.tsearch.datava.database.postgres.data.ChartData;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;

@Getter
@AllArgsConstructor
public class WebStatistics {
    private BoxStatistics boxStatistics;

    private ChartData<Long> highlightsPerMonth;
    private ChartData<Long> highlightsPerWeekday;

    private ChartData<Long> clipsPerMonth;
    private ChartData<Long> clipsPerHour;

    private ChartData<Long> clipsPerGame;

    private ChartData<Long> clipperPerCount;
    private ChartData<Long> clipperPerViews;

    private Instant calculatedAt;
}
