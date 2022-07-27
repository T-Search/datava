package de.tsearch.datava.web;

import de.tsearch.datava.database.postgres.data.*;
import de.tsearch.datava.database.postgres.entity.Broadcaster;
import de.tsearch.datava.database.postgres.repository.BroadcasterRepository;
import de.tsearch.datava.database.postgres.repository.ClipStatisticRepository;
import de.tsearch.datava.database.postgres.repository.HighlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("highlights/{creator}")
    public ResponseEntity<ChartData<Long>> highlightsStatistics(@PathVariable String creator) {
        Optional<Broadcaster> broadcasterOptional = broadcasterRepository.findByDisplayNameIgnoreCase(creator);
        if (broadcasterOptional.isPresent()) {
            Broadcaster broadcaster = broadcasterOptional.get();
            List<YearMonthStatistics> all = clipStatisticRepository.calculateYearMonthStatistics(broadcaster);
            return ResponseEntity.ok(generateChart("Highlights", all));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("highlightWeekday/{creator}")
    public ResponseEntity<ChartData<Long>> highlightWeekdayStatistics(@PathVariable String creator) {
        Optional<Broadcaster> broadcasterOptional = broadcasterRepository.findByDisplayNameIgnoreCase(creator);
        if (broadcasterOptional.isPresent()) {
            Broadcaster broadcaster = broadcasterOptional.get();
            List<WeekStatistics> weekStatistics = clipStatisticRepository.calculateWeekStatistics(broadcaster);
            return ResponseEntity.ok(generateChart("Clips", weekStatistics));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("clips/{creator}")
    public ResponseEntity<ChartData<Long>> clipsStatistics(@PathVariable String creator) {
        Optional<Broadcaster> broadcasterOptional = broadcasterRepository.findByDisplayNameIgnoreCase(creator);
        if (broadcasterOptional.isPresent()) {
            Broadcaster broadcaster = broadcasterOptional.get();
            List<YearMonthStatistics> all = clipStatisticRepository.calculateYearMonthStatistics(broadcaster);
            return ResponseEntity.ok(generateChart("Highlights", all));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("clipsHours/{creator}")
    public ResponseEntity<ChartData<Long>> clipHoursStatistics(@PathVariable String creator) {
        Optional<Broadcaster> broadcasterOptional = broadcasterRepository.findByDisplayNameIgnoreCase(creator);
        if (broadcasterOptional.isPresent()) {
            Broadcaster broadcaster = broadcasterOptional.get();
            List<HourStatistics> hourStatistics = clipStatisticRepository.calculateHourStatistics(broadcaster);
            return ResponseEntity.ok(generateChart("Highlights", hourStatistics));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("games/{creator}")
    public ResponseEntity<ChartData<Long>> gamesStatistics(@PathVariable String creator) {
        Optional<Broadcaster> broadcasterOptional = broadcasterRepository.findByDisplayNameIgnoreCase(creator);
        if (broadcasterOptional.isPresent()) {
            Broadcaster broadcaster = broadcasterOptional.get();
            List<GameStatistics> gameStatistics = clipStatisticRepository.calculateGameStatistics(broadcaster);

            List<String> labels = new ArrayList<>(Math.min(gameStatistics.size(), 10));
            List<Long> data = new ArrayList<>(Math.min(gameStatistics.size(), 10));

            String combinedName;
            long combinedCount = 0L;
            for (GameStatistics gameStatistic : gameStatistics) {
                //Add only the first 9 Games with name or all games if size is equals to 10
                if(labels.size() <= 9 || gameStatistics.size() == 10) {
                    if (gameStatistic.name() == null) {
                        labels.add("Unbekannt");
                    } else {
                        labels.add(gameStatistic.name());
                    }
                    data.add(gameStatistic.count());
                } else {
                    //Compute combined count
                    combinedCount += gameStatistic.count();
                }
            }
            if (combinedCount > 0) {
                combinedName = (gameStatistics.size() - labels.size()) + " andere Spiele";
                labels.add(combinedName);
                data.add(combinedCount);
            }

            return ResponseEntity.ok(new ChartData<>(labels, List.of(new DatasetData<>("Spiele", data))));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private ChartData<Long> generateChart(String datasetName, List<? extends ChartStatistics> chartStatistics) {
        List<String> labels = new ArrayList<>(chartStatistics.size());
        List<Long> data = new ArrayList<>(chartStatistics.size());
        for (ChartStatistics chartStatistic : chartStatistics) {
            labels.add(chartStatistic.getLabel());
            data.add(chartStatistic.getCount());
        }

        return new ChartData<>(labels, List.of(new DatasetData<>(datasetName, data)));
    }
}
