package de.tsearch.datava.web;

import de.tsearch.datava.database.postgres.entity.Broadcaster;
import de.tsearch.datava.database.postgres.entity.Clip;
import de.tsearch.datava.database.postgres.repository.BroadcasterRepository;
import de.tsearch.datava.database.postgres.repository.ClipRepository;
import de.tsearch.datava.web.entity.WebClip;
import de.tsearch.datava.web.entity.WebPage;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
@RequestMapping("clip")
@CrossOrigin(methods = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET, RequestMethod.DELETE}, origins = {"*"})
public class ClipController {

    private final ClipRepository clipRepository;
    private final BroadcasterRepository broadcasterRepository;

    public ClipController(ClipRepository clipRepository, BroadcasterRepository broadcasterRepository) {
        this.clipRepository = clipRepository;
        this.broadcasterRepository = broadcasterRepository;
    }

    @Cacheable("clipSearch")
    @GetMapping("search")
    public ResponseEntity<WebPage<WebClip>> searchClip(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "") String q,
            @RequestParam(name = "broadcaster") String broadcasterName
    ) {
        Optional<Broadcaster> broadcasterOptional = broadcasterRepository.findByDisplayNameIgnoreCase(broadcasterName);

        if (broadcasterOptional.isPresent()) {
            Page<Clip> clipPage;
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("viewCount").descending());
            if (q.length() == 0) {
                clipPage = clipRepository.findAllByBroadcaster(broadcasterOptional.get(), pageRequest);
            } else {
                clipPage = clipRepository.findAllByTitleContainingIgnoreCaseAndBroadcaster(q, broadcasterOptional.get(), pageRequest);
            }
            WebPage<WebClip> page = new WebPage<>();
            page.setContent(clipPage.getContent().stream().map(WebClip::new).collect(Collectors.toList()));
            page.setCurrentPage(clipPage.getNumber());
            page.setCurrentElements(clipPage.getNumberOfElements());
            page.setTotalPages(clipPage.getTotalPages());
            page.setTotalElements(clipPage.getTotalElements());

            CacheControl cacheControl = CacheControl.maxAge(60, TimeUnit.SECONDS).mustRevalidate();
            return ResponseEntity.ok().cacheControl(cacheControl).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
