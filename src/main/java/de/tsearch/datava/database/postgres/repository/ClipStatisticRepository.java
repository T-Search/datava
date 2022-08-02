package de.tsearch.datava.database.postgres.repository;

import de.tsearch.datava.database.postgres.data.*;
import de.tsearch.datava.database.postgres.entity.*;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@CacheConfig(cacheNames = "clipStatistic")
public interface ClipStatisticRepository extends CrudRepository<Clip, String> {
    Long countByBroadcaster(Broadcaster broadcaster);

    @Query(value = "SELECT count(id) FROM clip WHERE broadcaster_id = :broadcaster  AND created_at > (current_date - interval '30 days')", nativeQuery = true)
    Long countByBroadcasterLast30Days(Broadcaster broadcaster);

    @Query(value = "SELECT DISTINCT game FROM Clip WHERE broadcaster = :broadcaster AND game is not null")
    List<String> calculateGames(Broadcaster broadcaster);

    @Query("SELECT new de.tsearch.datava.database.postgres.data.ClipperData(creatorName, count(id) as co) FROM Clip c where broadcaster = :broadcaster GROUP BY creator_name ORDER BY co desc")
    List<ClipperData> calculateClipperByCount(Broadcaster broadcaster);

    @Query("SELECT new de.tsearch.datava.database.postgres.data.ClipperData(creatorName, sum(viewCount) as co) FROM Clip c where broadcaster = :broadcaster GROUP BY creator_name ORDER BY co desc")
    List<ClipperData> calculateClipperByViews(Broadcaster broadcaster);

    @Query("SELECT new de.tsearch.datava.database.postgres.data.GameStatistics(game, count(id) as co) FROM Clip c where broadcaster = :broadcaster GROUP BY game ORDER BY co desc, game")
    List<GameStatistics> calculateGameStatistics(Broadcaster broadcaster);

    @Query("SELECT new de.tsearch.datava.database.postgres.data.HourStatistics(extract (hour from created_at) as hour, COUNT(c)) FROM Clip c WHERE c.broadcaster = :broadcaster GROUP BY hour ORDER BY hour")
    List<HourStatistics> calculateHourStatistics(Broadcaster broadcaster);

    @Query("SELECT new de.tsearch.datava.database.postgres.data.WeekStatistics(extract (isodow from created_at) as weekday, COUNT(c)) FROM Clip c WHERE c.broadcaster = :broadcaster GROUP BY weekday ORDER BY weekday")
    List<WeekStatistics> calculateWeekStatistics(Broadcaster broadcaster);

    @Query(nativeQuery = true, name = "ClipYearMonthStatistics.calculateBroadcaster")
    List<YearMonthStatistics> calculateYearMonthStatistics(Broadcaster broadcaster);
}
