package de.tsearch.datava.web;

import de.tsearch.datava.database.postgres.entity.Clip;
import de.tsearch.datava.database.postgres.repository.ClipRepository;
import de.tsearch.datava.web.entity.WebClip;
import de.tsearch.datava.web.entity.WebPage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@RestController
@RequestMapping("clip")
public class ClipController {

    private final ClipRepository clipRepository;

    public ClipController(ClipRepository clipRepository) {
        this.clipRepository = clipRepository;
    }

    @GetMapping("search")
    public ResponseEntity<WebPage<WebClip>> searchClip(
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "20") int pageSize,
            @RequestParam(defaultValue = "") String q,
            @RequestParam(name = "broadcaster") String broadcasterName
    ) {
        Page<Clip> clipPage = clipRepository.findAllByTitleContainingIgnoreCaseAndBroadcasterDisplayNameIsIgnoreCase(q, broadcasterName, PageRequest.of(pageNumber, pageSize, Sort.by("viewCount").descending()));
        WebPage<WebClip> page = new WebPage<>();
        page.setContent(clipPage.getContent().stream().map(WebClip::new).collect(Collectors.toList()));
        page.setCurrentPage(clipPage.getNumber());
        page.setCurrentElements(clipPage.getNumberOfElements());
        page.setTotalPages(clipPage.getTotalPages());
        page.setTotalElements(clipPage.getTotalElements());

        return ResponseEntity.ok(page);
    }
}
