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
@NamedNativeQuery(name = "ClipYearMonthStatistics.calculateBroadcaster",
        query = "select TO_CHAR(created_at, 'YYYY-MM') as yearmonth, count(id) as count from clip where broadcaster_id = :broadcaster and created_at > (cast(date_trunc('month', current_date) as date) - interval '12 months') and created_at < date_trunc('month', current_date) group by yearmonth ORDER BY yearmonth",
        resultSetMapping = "Mapping.ClipYearMonthStatistics")
@SqlResultSetMapping(name = "Mapping.ClipYearMonthStatistics",
        classes = @ConstructorResult(targetClass = YearMonthStatistics.class,
                columns = {
                        @ColumnResult(name = "yearmonth", type = String.class),
                        @ColumnResult(name = "count", type = Long.class)
                }))
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
