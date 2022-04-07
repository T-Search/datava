package de.tsearch.datava.web.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebPage<T> {
    private List<T> content;
    private long totalElements;
    private int currentElements;
    private int totalPages;
    private int currentPage;
}
