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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
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
            @RequestParam(name = "broadcaster") String broadcasterName,
            @RequestParam(name = "sortOrder", defaultValue = "desc") String sortOrder,
            @RequestParam(name = "sortProperty", defaultValue = "views") String sortProperty,
            @RequestParam(name = "game", defaultValue = "") String game,
            @RequestParam(name = "creator", defaultValue = "") String creator,
            @RequestParam(name = "views", defaultValue = "0") long views,
            @RequestParam(name = "viewsOperator", defaultValue = ">=") String viewOperator
    ) {
        Optional<Broadcaster> broadcasterOptional = broadcasterRepository.findByDisplayNameIgnoreCase(broadcasterName);

        if (broadcasterOptional.isPresent()) {
            Page<Clip> clipPage;

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
                    clipPage = clipRepository.findAllByBroadcasterAndGameContainingIgnoreCaseAndCreatorNameContainingIgnoreCaseAndViewCountGreaterThanEqual(broadcasterOptional.get(), game, creator, views, pageRequest);
                } else if ("<=".equals(viewOperator)) {
                    clipPage = clipRepository.findAllByBroadcasterAndGameContainingIgnoreCaseAndCreatorNameContainingIgnoreCaseAndViewCountLessThanEqual(broadcasterOptional.get(), game, creator, views, pageRequest);
                } else {
                    return ResponseEntity.badRequest().build();
                }
            } else {
                if (">=".equals(viewOperator)) {
                    clipPage = clipRepository.findAllByTitleContainingIgnoreCaseAndBroadcasterAndGameContainingIgnoreCaseAndCreatorNameContainingIgnoreCaseAndViewCountGreaterThanEqual(q, broadcasterOptional.get(), game, creator, views, pageRequest);
                } else if ("<=".equals(viewOperator)) {
                    clipPage = clipRepository.findAllByTitleContainingIgnoreCaseAndBroadcasterAndGameContainingIgnoreCaseAndCreatorNameContainingIgnoreCaseAndViewCountLessThanEqual(q, broadcasterOptional.get(), game, creator, views, pageRequest);
                } else {
                    return ResponseEntity.badRequest().build();
                }
            }
            WebPage<WebClip> page = new WebPage<>();
            page.setContent(clipPage.getContent().stream().map(WebClip::new).collect(Collectors.toList()));
            page.setCurrentPage(clipPage.getNumber());
            page.setCurrentElements(clipPage.getNumberOfElements());
            page.setTotalPages(clipPage.getTotalPages());
            page.setTotalElements(clipPage.getTotalElements());

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
