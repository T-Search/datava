package de.tsearch.datava.web;

import de.tsearch.datava.database.postgres.entity.Broadcaster;
import de.tsearch.datava.database.postgres.entity.Highlight;
import de.tsearch.datava.database.postgres.repository.BroadcasterRepository;
import de.tsearch.datava.database.postgres.repository.HighlightRepository;
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
            @RequestParam(name = "broadcaster") String broadcasterName,
            @RequestParam(name = "sortOrder", defaultValue = "desc") String sortOrder,
            @RequestParam(name = "sortProperty", defaultValue = "views") String sortProperty,
            @RequestParam(name = "views", defaultValue = "0") long views,
            @RequestParam(name = "viewsOperator", defaultValue = ">=") String viewOperator
    ) {
        Optional<Broadcaster> broadcasterOptional = broadcasterRepository.findByDisplayNameIgnoreCase(broadcasterName);

        if (broadcasterOptional.isPresent()) {
            Page<Highlight> highlightPage;

            //Build sort
            Sort sort;
            try {
                switch (sortProperty) {
                    case "date":
                        sort = setOrder(Sort.by("createdAt"), sortOrder);
                        break;
                    case "views":
                        sort = setOrder(Sort.by("viewCount"), sortOrder).and(Sort.by("createdAt").descending());
                        break;
                    default:
                        return ResponseEntity.badRequest().build();
                }
            } catch (RuntimeException e) { //Sort order not valid!
                return ResponseEntity.badRequest().build();
            }

            PageRequest pageRequest = PageRequest.of(pageNumber, pageSize, sort);

            if (q.length() == 0) {
                if (">=".equals(viewOperator)) {
                    highlightPage = highlightRepository.findAllByBroadcasterAndViewCountGreaterThanEqual(broadcasterOptional.get(), views, pageRequest);
                } else if ("<=".equals(viewOperator)) {
                    highlightPage = highlightRepository.findAllByBroadcasterAndViewCountLessThanEqual(broadcasterOptional.get(), views, pageRequest);
                } else {
                    return ResponseEntity.badRequest().build();
                }
            } else {
                if (">=".equals(viewOperator)) {
                    highlightPage = highlightRepository.findAllByTitleContainingIgnoreCaseAndBroadcasterAndViewCountGreaterThanEqual(q, broadcasterOptional.get(), views, pageRequest);
                } else if ("<=".equals(viewOperator)) {
                    highlightPage = highlightRepository.findAllByTitleContainingIgnoreCaseAndBroadcasterAndViewCountLessThanEqual(q, broadcasterOptional.get(), views, pageRequest);
                } else {
                    return ResponseEntity.badRequest().build();
                }
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

    private Sort setOrder(Sort sort, String order) {
        if ("asc".equalsIgnoreCase(order)) {
            return sort.ascending();
        } else if ("desc".equalsIgnoreCase(order)) {
            return sort.descending();
        } else {
            throw new RuntimeException("Sort order must be asc or desc");
        }
    }
}
