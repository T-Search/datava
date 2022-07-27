package de.tsearch.datava.database.postgres.data;

import java.util.List;

public record ChartData<T>(List<String> labels, List<DatasetData<T>> datasets) {
}
