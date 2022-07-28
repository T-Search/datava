package de.tsearch.datava.database.postgres.data;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

public record BoxStatistics(long highlights, long highlightsLast30Days, long clips, long clipsLast30Days, long games) implements Serializable {}
