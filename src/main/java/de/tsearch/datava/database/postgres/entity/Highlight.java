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
public class Highlight {
    @Id
    private long id;

    @ManyToOne
    private Broadcaster broadcaster;

    @Column
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private Date createdAt;

    @Column
    private Date publishedAt;

    @Column
    private String thumbnailUrl;

    @Column
    private long viewCount;

    @Column
    private String language;

    @Column
    private String duration;
}
