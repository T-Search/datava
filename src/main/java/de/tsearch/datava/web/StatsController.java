package de.tsearch.datava.web;

import de.tsearch.datava.database.postgres.data.*;
import de.tsearch.datava.database.postgres.entity.Broadcaster;
import de.tsearch.datava.database.postgres.repository.BroadcasterRepository;
import de.tsearch.datava.database.postgres.repository.ClipStatisticRepository;
import de.tsearch.datava.database.postgres.repository.HighlightRepository;
import de.tsearch.datava.database.postgres.repository.HighlightStatisticRepository;
import de.tsearch.datava.web.entity.WebStatistics;
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
    private final HighlightStatisticRepository highlightStatisticRepository;
    private final HighlightRepository highlightRepository;

    @GetMapping("{creator}")
    private ResponseEntity<WebStatistics> creatorStatistics(@PathVariable String creator) {
        Optional<Broadcaster> broadcasterOptional = broadcasterRepository.findByDisplayNameIgnoreCase(creator);

        if (broadcasterOptional.isEmpty()) return ResponseEntity.notFound().build();
        Broadcaster broadcaster = broadcasterOptional.get();

        //Box Statistics
        BoxStatistics boxStatistics = new BoxStatistics(
                highlightStatisticRepository.countByBroadcaster(broadcaster),
                highlightStatisticRepository.countByBroadcasterLast30Days(broadcaster),
                clipStatisticRepository.countByBroadcaster(broadcaster),
                clipStatisticRepository.countByBroadcasterLast30Days(broadcaster),
                clipStatisticRepository.calculateGames(broadcaster).size()
        );

        //Highlights per Month
        List<YearMonthStatistics> highlightYearMonthStatistics = highlightStatisticRepository.calculateYearMonthStatistics(broadcaster);
        ChartData<Long> highlightsPerMonth = generateChart("Highlights", highlightYearMonthStatistics);
        //Highlights per Weekday
        List<WeekStatistics> weekStatistics = highlightStatisticRepository.calculateWeekStatistics(broadcaster);
        ChartData<Long> highlightsPerWeekday = generateChart("Clips", weekStatistics);

        //Clips per Month
        List<YearMonthStatistics> clipYearMonthStatistics = clipStatisticRepository.calculateYearMonthStatistics(broadcaster);
        ChartData<Long> clipsPerMonth = generateChart("Highlights", clipYearMonthStatistics);
        //Clips per Hour
        List<HourStatistics> hourStatistics = clipStatisticRepository.calculateHourStatistics(broadcaster);
        ChartData<Long> clipsPerHour = generateChart("Highlights", hourStatistics);
        //Clips per Game
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

        ChartData<Long> clipsPerGame = new ChartData<>(labels, List.of(new DatasetData<>("Spiele", data)));

        return ResponseEntity.ok(new WebStatistics(
                boxStatistics,
                highlightsPerMonth,
                highlightsPerWeekday,
                clipsPerMonth,
                clipsPerHour,
                clipsPerGame
        ));
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
