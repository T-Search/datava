package de.tsearch.datava.web;

import de.tsearch.datava.database.postgres.data.BoxStatistics;
import de.tsearch.datava.database.postgres.entity.Broadcaster;
import de.tsearch.datava.database.postgres.entity.YearMonthStatistics;
import de.tsearch.datava.database.postgres.repository.BroadcasterRepository;
import de.tsearch.datava.database.postgres.repository.ClipStatisticRepository;
import de.tsearch.datava.database.postgres.repository.HighlightRepository;
import de.tsearch.datava.web.entity.chart.Chart;
import de.tsearch.datava.web.entity.chart.Dataset;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.*;

@RestController
@RequiredArgsConstructor
@CacheConfig(cacheNames = "test")
@RequestMapping("stats")
public class StatsController {

    private final BroadcasterRepository broadcasterRepository;
    private final ClipStatisticRepository clipStatisticRepository;
    private final HighlightRepository highlightRepository;

    @GetMapping("box/{creator}")
    public ResponseEntity<?> boxStatistics(@PathVariable String creator) {
        Optional<Broadcaster> broadcasterOptional = broadcasterRepository.findByDisplayNameIgnoreCase(creator);
        if (broadcasterOptional.isPresent()) {
            Broadcaster broadcaster = broadcasterOptional.get();
            return ResponseEntity.ok(new BoxStatistics(
                    highlightRepository.countByBroadcaster(broadcaster),
                    highlightRepository.countByBroadcasterLast30Days(broadcaster),
                    clipStatisticRepository.countByBroadcaster(broadcaster),
                    clipStatisticRepository.countByBroadcasterLast30Days(broadcaster),
                    clipStatisticRepository.calculateGames(broadcaster).size()
            ));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("test")
    public ResponseEntity<?> test() throws ParseException {
        //TODO Add Creator check
        List<YearMonthStatistics> all = clipStatisticRepository.calculateYearMonthStatistics();
        List<String> labels = extractDisplayLabels(all);

        Dataset<Float> allPercentDataset = new Dataset<>("Andere Streamer %", calculateAsPercentage(all.stream().map(YearMonthStatistics::getCount).toList()), "#e15759", "#e15759", "y1", "line", false);
        Broadcaster gammalunatic = broadcasterRepository.findByDisplayNameIgnoreCase("gammalunatic").get();
        List<YearMonthStatistics> creator = clipStatisticRepository.calculateYearMonthStatistics(gammalunatic);
        //TODO Replace Creator with creator name
        Dataset<Long> creatorDataset = new Dataset<>("Creator", creator.stream().map(YearMonthStatistics::getCount).toList(), "#f28e2b", "#f28e2b", "y", "bar", false);
        Dataset<Float> creatorPercentDataset = new Dataset<>("Creator %", calculateAsPercentage(creator.stream().map(YearMonthStatistics::getCount).toList()), "#7f4f1e", "#7f4f1e", "y1", "line", false);

        Chart yearMonthChart = new Chart(labels, List.of(creatorDataset, creatorPercentDataset, allPercentDataset));


        return ResponseEntity.ok(yearMonthChart);
    }


    private List<String> extractDisplayLabels(List<YearMonthStatistics> statistics) {
        ArrayList<String> labels = new ArrayList<>(statistics.size());
        Calendar calendar = Calendar.getInstance();
        for (YearMonthStatistics yearMonthStatistic : statistics) {
            calendar.setTime(yearMonthStatistic.getYearMonth());
            labels.add(calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.GERMAN) + " " + calendar.get(Calendar.YEAR));
        }
        return labels;
    }

    private List<Float> calculateAsPercentage(List<Long> longList) {
        List<Float> percentages = new ArrayList<>(longList.size());
        long sum = longList.stream().mapToLong(aLong -> aLong).sum();

        //Copied and modified https://stackoverflow.com/a/10416314/13231742
        BigDecimal d = new BigDecimal(sum);
        BigDecimal i = new BigDecimal(100);
        for (Long l : longList) {
            BigDecimal n = new BigDecimal(l);

            Float percentage = n.multiply(i).divide(d, 2, RoundingMode.HALF_UP).floatValue();
            percentages.add(percentage);
        }

        return percentages;
    }

}
