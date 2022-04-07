package de.tsearch.datava.database.postgres.repository;

import de.tsearch.datava.database.postgres.entity.Broadcaster;
import org.springframework.data.repository.CrudRepository;

public interface BroadcasterRepository extends CrudRepository<Broadcaster, Long> {
}
