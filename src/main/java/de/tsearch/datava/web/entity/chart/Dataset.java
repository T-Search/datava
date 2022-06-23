package de.tsearch.datava.web.entity.chart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Dataset<T> {
    private String label;
    private List<T> data;
    @JsonProperty("backgroundColor")
    private String color;
    private boolean hidden;
}
