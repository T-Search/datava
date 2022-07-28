package de.tsearch.datava.database.postgres.repository;

import de.tsearch.datava.database.postgres.data.WeekStatistics;
import de.tsearch.datava.database.postgres.data.YearMonthStatistics;
import de.tsearch.datava.database.postgres.entity.Broadcaster;
import de.tsearch.datava.database.postgres.entity.Highlight;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HighlightStatisticRepository extends CrudRepository<Highlight, Long> {
    Long countByBroadcaster(Broadcaster broadcaster);

    @Query(value = "SELECT count(id) FROM highlight WHERE broadcaster_id = :broadcaster  AND created_at > (current_date - interval '30 days')", nativeQuery = true)
    Long countByBroadcasterLast30Days(Broadcaster broadcaster);

    @Query("SELECT new de.tsearch.datava.database.postgres.data.WeekStatistics(extract (isodow from created_at) as weekday, COUNT(c)) FROM Clip c WHERE c.broadcaster = :broadcaster GROUP BY weekday ORDER BY weekday")
    List<WeekStatistics> calculateWeekStatistics(Broadcaster broadcaster);

    @Query(nativeQuery = true, name = "HighlightYearMonthStatistics.calculateBroadcaster")
    List<YearMonthStatistics> calculateYearMonthStatistics(Broadcaster broadcaster);
}
