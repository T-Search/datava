package de.tsearch.datava.database.postgres.repository;

import de.tsearch.datava.database.postgres.entity.Broadcaster;
import de.tsearch.datava.database.postgres.entity.Clip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface ClipRepository extends CrudRepository<Clip, String> {
    Page<Clip> findAllByTitleContainingIgnoreCaseAndBroadcasterAndGameContainingIgnoreCaseAndCreatorNameContainingIgnoreCaseAndViewCountGreaterThanEqual(String title, Broadcaster broadcaster, String game, String creator, long views, Pageable pageable);

    Page<Clip> findAllByTitleContainingIgnoreCaseAndBroadcasterAndGameContainingIgnoreCaseAndCreatorNameContainingIgnoreCaseAndViewCountLessThanEqual(String title, Broadcaster broadcaster, String game, String creator, long views, Pageable pageable);

    Page<Clip> findAllByBroadcasterAndGameContainingIgnoreCaseAndCreatorNameContainingIgnoreCaseAndViewCountGreaterThanEqual(Broadcaster broadcaster, String game, String creator, long views, Pageable pageable);

    Page<Clip> findAllByBroadcasterAndGameContainingIgnoreCaseAndCreatorNameContainingIgnoreCaseAndViewCountLessThanEqual(Broadcaster broadcaster, String game, String creator, long views, Pageable pageable);
}
