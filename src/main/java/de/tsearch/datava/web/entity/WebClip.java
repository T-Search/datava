package de.tsearch.datava.web.entity;

import de.tsearch.datava.database.postgres.entity.Clip;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Date;

@Getter
public class WebClip {
    private final String id;

    private final String broadcasterName;

    private final String creatorName;

    private final Long videoId;

    private final String game;

    private final String language;

    private final String title;

    private final Long viewCount;

    private final Date createdAt;

    private final String thumbnailUrl;

    private final double duration;

    public WebClip(Clip clip) {
        this.id = clip.getId();
        this.broadcasterName = clip.getBroadcaster().getDisplayName();
        this.creatorName = clip.getCreatorName();
        this.videoId = clip.getVideoId();
        this.game = clip.getGame();
        this.language = clip.getLanguage();
        this.title = clip.getTitle();
        this.viewCount = clip.getViewCount();
        this.createdAt = clip.getCreatedAt();
        this.thumbnailUrl = clip.getThumbnailUrl();
        this.duration = clip.getDuration();
    }
}
