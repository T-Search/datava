package de.tsearch.datava.web;

import de.tsearch.datava.database.postgres.entity.Broadcaster;
import de.tsearch.datava.database.postgres.entity.Clip;
import de.tsearch.datava.database.postgres.entity.Highlight;
import de.tsearch.datava.database.postgres.repository.BroadcasterRepository;
import de.tsearch.datava.database.postgres.repository.ClipRepository;
import de.tsearch.datava.database.postgres.repository.HighlightRepository;
import de.tsearch.datava.web.entity.WebClip;
import de.tsearch.datava.web.entity.WebHighlight;
import de.tsearch.datava.web.entity.WebPage;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("highlight")
@CrossOrigin(methods = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET, RequestMethod.DELETE}, origins = {"*"})
public class HighlightController {

    private final HighlightRepository highlightRepository;
    private final BroadcasterRepository broadcasterRepository;

    public HighlightController(HighlightRepository highlightRepository, BroadcasterRepository broadcasterRepository) {
        this.highlightRepository = highlightRepository;
        this.broadcasterRepository = broadcasterRepository;
    }

    @Cacheable("highlightSearch")
    @GetMapping("search")
    public ResponseEntity<WebPage<WebHighlight>> searchHighlight(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "") String q,
            @RequestParam(name = "broadcaster") String broadcasterName
    ) {
        Optional<Broadcaster> broadcasterOptional = broadcasterRepository.findByDisplayNameIgnoreCase(broadcasterName);

        if (broadcasterOptional.isPresent()) {
            Page<Highlight> highlightPage;
            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, Sort.by("viewCount").descending());
            if (q.length() == 0) {
                highlightPage = highlightRepository.findAllByBroadcaster(broadcasterOptional.get(), pageRequest);
            } else {
                highlightPage = highlightRepository.findAllByTitleContainingIgnoreCaseAndBroadcaster(q, broadcasterOptional.get(), pageRequest);
            }
            WebPage<WebHighlight> page = new WebPage<>();
            page.setContent(highlightPage.getContent().stream().map(WebHighlight::new).collect(Collectors.toList()));
            page.setCurrentPage(highlightPage.getNumber());
            page.setCurrentElements(highlightPage.getNumberOfElements());
            page.setTotalPages(highlightPage.getTotalPages());
            page.setTotalElements(highlightPage.getTotalElements());

            return ResponseEntity.ok(page);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
