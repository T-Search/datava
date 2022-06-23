package de.tsearch.datava.web.entity.chart;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


//TODO Split in Line and Bar Dataset
@Data
@AllArgsConstructor
public class Dataset<T> {
    private String label;
    private List<T> data;
    @JsonProperty("backgroundColor")
    private String color;
    @JsonProperty("borderColor")
    private String borderColor;
    @JsonProperty("yAxisID")
    private String yAxisID;
    private String type;
    private boolean hidden;
}
