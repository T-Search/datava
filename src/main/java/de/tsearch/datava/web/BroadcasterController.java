package de.tsearch.datava.web;

import de.tsearch.datava.database.postgres.entity.Broadcaster;
import de.tsearch.datava.database.postgres.repository.BroadcasterRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
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
    @GetMapping("autocomplete")
    public List<String> getAutocompleteBroadcaster() {
        return StreamSupport.stream(broadcasterRepository.findByVipIsTrueOrTwitchAuthorisedIsTrue().spliterator(), false).map(Broadcaster::getDisplayName).filter(Objects::nonNull).toList();
    }
}
