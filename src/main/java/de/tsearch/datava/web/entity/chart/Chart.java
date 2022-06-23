package de.tsearch.datava.web.entity.chart;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Chart {
    private List<String> labels;
    private List<Dataset> datasets;
}
