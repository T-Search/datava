package de.tsearch.datava.database.postgres.repository;

import de.tsearch.datava.database.postgres.entity.Broadcaster;
import de.tsearch.datava.database.postgres.entity.Clip;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

public interface ClipRepository extends CrudRepository<Clip, String> {
    Page<Clip> findAllByTitleContainingIgnoreCaseAndBroadcaster(String title, Broadcaster broadcaster, Pageable pageable);
}
