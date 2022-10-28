package de.tsearch.datava.database.postgres.entity;

import de.tsearch.datava.database.postgres.data.YearMonthStatistics;
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
@NamedNativeQuery(name = "HighlightYearMonthStatistics.calculateBroadcaster",
        query = "select TO_CHAR(created_at, 'YYYY-MM') as yearmonth, count(id) as count from highlight where broadcaster_id = :broadcaster and created_at > (cast(date_trunc('month', current_date) as date) - interval '12 months') and created_at < date_trunc('month', current_date) group by yearmonth ORDER BY yearmonth",
        resultSetMapping = "Mapping.HighlightYearMonthStatistics")
@SqlResultSetMapping(name = "Mapping.HighlightYearMonthStatistics",
        classes = @ConstructorResult(targetClass = YearMonthStatistics.class,
                columns = {
                        @ColumnResult(name = "yearmonth", type = String.class),
                        @ColumnResult(name = "count", type = Long.class)
                }))
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
