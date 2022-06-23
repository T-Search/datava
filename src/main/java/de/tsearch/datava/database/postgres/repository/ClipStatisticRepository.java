package de.tsearch.datava.database.postgres.repository;

import de.tsearch.datava.database.postgres.entity.*;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@CacheConfig(cacheNames = "clipStatistic")
public interface ClipStatisticRepository extends CrudRepository<Clip, String> {

    @Cacheable
    @Query("SELECT new de.tsearch.datava.database.postgres.entity.HourStatistics(extract (hour from created_at) as hour, COUNT(c)) FROM Clip c GROUP BY hour ORDER BY hour")
    List<HourStatistics> calculateHourStatistics();

    @Cacheable(key = "#broadcaster.id")
    @Query("SELECT new de.tsearch.datava.database.postgres.entity.HourStatistics(extract (hour from created_at) as hour, COUNT(c)) FROM Clip c WHERE c.broadcaster = :broadcaster GROUP BY hour ORDER BY hour")
    List<HourStatistics> calculateHourStatistics(Broadcaster broadcaster);

    @Cacheable
    @Query("SELECT new de.tsearch.datava.database.postgres.entity.WeekStatistics(extract (isodow from created_at) as weekday, COUNT(c)) FROM Clip c GROUP BY weekday ORDER BY weekday")
    List<WeekStatistics> calculateWeekStatistics();

    @Cacheable(key = "#broadcaster.id")
    @Query("SELECT new de.tsearch.datava.database.postgres.entity.WeekStatistics(extract (isodow from created_at) as weekday, COUNT(c)) FROM Clip c WHERE c.broadcaster = :broadcaster GROUP BY weekday ORDER BY weekday")
    List<WeekStatistics> calculateWeekStatistics(Broadcaster broadcaster);

    @Cacheable
    @Query(nativeQuery = true, name = "YearMonthStatistics.calculateAll")
    List<YearMonthStatistics> calculateYearMonthStatistics();

    @Cacheable(key = "#broadcaster.id")
    @Query(nativeQuery = true, name = "YearMonthStatistics.calculateBroadcaster")
    List<YearMonthStatistics> calculateYearMonthStatistics(Broadcaster broadcaster);
}
