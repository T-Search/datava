package de.tsearch.datava.database.postgres.repository;

import de.tsearch.datava.database.postgres.entity.Broadcaster;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BroadcasterRepository extends CrudRepository<Broadcaster, Long> {

    Iterable<Broadcaster> findByVipIsTrueOrTwitchAuthorisedIsTrue();

    Optional<Broadcaster> findByDisplayNameIgnoreCase(String displayName);
}
