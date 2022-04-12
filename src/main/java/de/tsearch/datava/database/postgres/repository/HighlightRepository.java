package de.tsearch.datava.database.postgres.repository;


import de.tsearch.datava.database.postgres.entity.Broadcaster;
import de.tsearch.datava.database.postgres.entity.Clip;
import de.tsearch.datava.database.postgres.entity.Highlight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface HighlightRepository extends CrudRepository<Highlight, Long> {
    Page<Highlight> findAllByTitleContainingIgnoreCaseAndBroadcaster(String title, Broadcaster broadcaster, Pageable pageable);
    Page<Highlight> findAllByBroadcaster(Broadcaster broadcaster, Pageable pageable);
}
