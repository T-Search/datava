package de.tsearch.datava.database.postgres.data;

import lombok.Getter;
import lombok.ToString;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

@ToString
@Getter
public class YearMonthStatistics implements ChartStatistics {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
    private final Calendar calendar = Calendar.getInstance();

    private Date yearMonth;
    private long count;

    public YearMonthStatistics(String yearMonthStr, Long count) {
        try {
            this.yearMonth = sdf.parse(yearMonthStr);
        } catch (ParseException e) {
            throw new RuntimeException("Cannot parse yyyy-MM", e);
        }
        this.count = count;
    }

    @Override
    public String getLabel() {
        calendar.setTime(yearMonth);
        return calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.GERMAN) + " " + calendar.get(Calendar.YEAR);
    }
}
