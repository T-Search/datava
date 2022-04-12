package de.tsearch.datava.web.entity;

import de.tsearch.datava.database.postgres.entity.Highlight;
import lombok.Getter;

import java.util.Date;

@Getter
public class WebHighlight {
    private final long id;

    private final String broadcasterName;

    private final String title;

    private final String description;

    private final Date createdAt;

    private final Date publishedAt;

    private final String thumbnailUrl;

    private final long viewCount;

    private final String language;

    private final String duration;

    public WebHighlight(Highlight highlight) {
        this.id = highlight.getId();
        this.broadcasterName = highlight.getBroadcaster().getDisplayName();
        this.title = highlight.getTitle();
        this.description = highlight.getDescription();
        this.createdAt = highlight.getCreatedAt();
        this.publishedAt = highlight.getPublishedAt();
        String thumb = highlight.getThumbnailUrl();
        if (thumb.contains("%{width}")) {
            thumb = thumb.replace("%{width}", "480");
        }
        if (thumb.contains("%{height}")) {
            thumb = thumb.replace("%{height}", "272");
        }
        this.thumbnailUrl = thumb;
        this.viewCount = highlight.getViewCount();
        this.language = highlight.getLanguage();
        this.duration = highlight.getDuration();
    }
}
