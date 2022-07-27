package de.tsearch.datava.database.postgres.data;

import java.util.List;

public record DatasetData<T>(String name, List<T> data) {
}
