package de.tsearch.datava.database.postgres.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Clip {
    @Id
    private String id;

    @ManyToOne
    private Broadcaster broadcaster;

    @Column
    private String creatorId;

    @Column
    private String creatorName;

    @Column
    private Long videoId;

    @Column
    private String game;

    @Column
    private String language;

    @Column
    private String title;

    @Column
    private Long viewCount;

    @Column
    private Date createdAt;

    @Column
    private String thumbnailUrl;

    @Column
    private double duration;
}
