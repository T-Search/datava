package de.tsearch.datava.web;

import de.tsearch.datava.database.postgres.entity.Broadcaster;
import de.tsearch.datava.database.postgres.repository.BroadcasterRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

@RestController
@RequestMapping("broadcaster")
@CrossOrigin(methods = {RequestMethod.GET}, origins = {"*"})
public class BroadcasterController {

    private final BroadcasterRepository broadcasterRepository;

    public BroadcasterController(BroadcasterRepository broadcasterRepository) {
        this.broadcasterRepository = broadcasterRepository;
    }

    @Cacheable("broadcasterNames")
    @GetMapping("all")
    public ResponseEntity<List<String>> getAllBroadcaster() {
        CacheControl cacheControl = CacheControl.maxAge(60, TimeUnit.SECONDS).mustRevalidate();
        List<String> body = StreamSupport.stream(broadcasterRepository.findAll().spliterator(), false).map(Broadcaster::getDisplayName).filter(Objects::nonNull).toList();
        return ResponseEntity.ok().cacheControl(cacheControl).body(body);
    }
}
