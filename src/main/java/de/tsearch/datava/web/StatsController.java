package de.tsearch.datava.web;

import de.tsearch.datava.database.postgres.data.*;
import de.tsearch.datava.database.postgres.entity.Broadcaster;
import de.tsearch.datava.database.postgres.repository.BroadcasterRepository;
import de.tsearch.datava.database.postgres.repository.ClipStatisticRepository;
import de.tsearch.datava.database.postgres.repository.HighlightStatisticRepository;
import de.tsearch.datava.web.entity.WebStatistics;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@CacheConfig(cacheNames = "statistic")
@RequestMapping("stats")
@CrossOrigin(methods = {RequestMethod.POST, RequestMethod.PUT, RequestMethod.GET, RequestMethod.DELETE}, origins = {"*"})
public class StatsController {

    private final BroadcasterRepository broadcasterRepository;
    private final ClipStatisticRepository clipStatisticRepository;
    private final HighlightStatisticRepository highlightStatisticRepository;

    @Cacheable
    @GetMapping("{creator}")
    public ResponseEntity<WebStatistics> creatorStatistics(@PathVariable String creator) {
        Instant calculatedAt = Instant.now();
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
        ChartData<Long> highlightsPerWeekday = generateChart("Highlights", weekStatistics);

        //Clips per Month
        List<YearMonthStatistics> clipYearMonthStatistics = clipStatisticRepository.calculateYearMonthStatistics(broadcaster);
        ChartData<Long> clipsPerMonth = generateChart("Clips", clipYearMonthStatistics);
        //Clips per Hour
        List<HourStatistics> hourStatistics = clipStatisticRepository.calculateHourStatistics(broadcaster);
        ChartData<Long> clipsPerHour = generateChart("Clips", hourStatistics);
        //Clips per Game
        List<GameStatistics> gameStatistics = clipStatisticRepository.calculateGameStatistics(broadcaster);
        ChartData<Long> clipsPerGame = combineChartData("Spiele", gameStatistics, 10);

        //Clipper by Count
        List<ClipperData> clipperDataByCount = clipStatisticRepository.calculateClipperByCount(broadcaster);
        ChartData<Long> clipperByCount = combineChartData("Clipper", clipperDataByCount, 10);

        //Clipper by Views
        List<ClipperData> clipperDataByViews = clipStatisticRepository.calculateClipperByViews(broadcaster);
        ChartData<Long> clipperByViews = combineChartData("Clipper", clipperDataByViews, 10);

        return ResponseEntity.ok(new WebStatistics(
                boxStatistics,
                highlightsPerMonth,
                highlightsPerWeekday,
                clipsPerMonth,
                clipsPerHour,
                clipsPerGame,
                clipperByCount,
                clipperByViews,
                calculatedAt
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

    private ChartData<Long> combineChartData(String datasetName, List<? extends ChartStatistics> chartStatistics, int cutAfter) {
        List<String> labels = new ArrayList<>(Math.min(chartStatistics.size(), cutAfter));
        List<Long> data = new ArrayList<>(Math.min(chartStatistics.size(), cutAfter));

        String combinedName;
        long combinedCount = 0L;
        for (ChartStatistics chartStatistic : chartStatistics) {
            if(labels.size() <= (cutAfter - 1) || chartStatistics.size() == cutAfter) {
                if (chartStatistic.getLabel() == null || chartStatistic.getLabel().isBlank()) {
                    labels.add("Unbekannt");
                } else {
                    labels.add(chartStatistic.getLabel());
                }
                data.add(chartStatistic.getCount());
            } else {
                //Compute combined count
                combinedCount += chartStatistic.getCount();
            }
        }
        if (combinedCount > 0) {
            combinedName = (chartStatistics.size() - labels.size()) + " andere " + datasetName;
            labels.add(combinedName);
            data.add(combinedCount);
        }

        return new ChartData<>(labels, List.of(new DatasetData<>(datasetName, data)));
    }
}
