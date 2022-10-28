package de.tsearch.datava.database.postgres.data;

import lombok.Getter;
import lombok.ToString;

import java.text.DateFormatSymbols;
import java.util.Locale;

@Getter
@ToString
public class WeekStatistics implements ChartStatistics {
    private static final DateFormatSymbols SYMBOLS = DateFormatSymbols.getInstance(Locale.GERMAN);
    private WeekdayEnum weekday;
    private long count;

    public WeekStatistics(Integer weekdayNumber, Long count) {
        this.weekday = WeekdayEnum.values()[weekdayNumber];
        this.count = count;
    }

    @Override
    public String getLabel() {
        for (int i = 0; i < WeekdayEnum.values().length; i++) {
            if (weekday.equals(WeekdayEnum.values()[i])) {
                int weekdayNumberFromJavaSymbly = ((i + 1) % WeekdayEnum.values().length);
                if (weekdayNumberFromJavaSymbly == 0) weekdayNumberFromJavaSymbly++;
                return SYMBOLS.getWeekdays()[weekdayNumberFromJavaSymbly];
            }
        }

        return weekday.name();
    }

    public enum WeekdayEnum {
        INVALID, MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
    }
}
