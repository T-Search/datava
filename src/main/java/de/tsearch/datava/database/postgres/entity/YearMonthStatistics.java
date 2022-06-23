package de.tsearch.datava.database.postgres.entity;

import lombok.Getter;
import lombok.ToString;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@ToString
@Getter
public class YearMonthStatistics {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
    private Date yearMonth;
    private Long count;

    public YearMonthStatistics(String yearMonthStr, Long count) {
        try {
            this.yearMonth = sdf.parse(yearMonthStr);
        } catch (ParseException e) {
            throw new RuntimeException("Cannot parse yyyy-MM", e);
        }
        this.count = count;
    }
}
