package de.tsearch.datava.database.postgres.repository;


import de.tsearch.datava.database.postgres.entity.Broadcaster;
import de.tsearch.datava.database.postgres.entity.Highlight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface HighlightRepository extends CrudRepository<Highlight, Long> {
    Page<Highlight> findAllByTitleContainingIgnoreCaseAndBroadcasterAndViewCountGreaterThanEqual(String title, Broadcaster broadcaster, long views, Pageable pageable);

    Page<Highlight> findAllByTitleContainingIgnoreCaseAndBroadcasterAndViewCountLessThanEqual(String title, Broadcaster broadcaster, long views, Pageable pageable);

    Page<Highlight> findAllByBroadcasterAndViewCountGreaterThanEqual(Broadcaster broadcaster, long views, Pageable pageable);

    Page<Highlight> findAllByBroadcasterAndViewCountLessThanEqual(Broadcaster broadcaster, long views, Pageable pageable);

    Long countByBroadcaster(Broadcaster broadcaster);

    @Query(value = "SELECT count(id) FROM highlight WHERE broadcaster_id = :broadcaster  AND created_at > (current_date - interval '30 days')", nativeQuery = true)
    Long countByBroadcasterLast30Days(Broadcaster broadcaster);
}
