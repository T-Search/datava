package de.tsearch.datava;

import de.tsearch.datava.database.postgres.repository.BroadcasterRepository;
import de.tsearch.datava.database.postgres.repository.ClipStatisticRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;

@EnableCaching
@SpringBootApplication
public class DatavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatavaApplication.class, args);
    }

    @Bean
    CommandLineRunner afterStart(ClipStatisticRepository clipStatisticRepository, BroadcasterRepository broadcasterRepository) {
        return args -> {
//            Broadcaster broadcaster = broadcasterRepository.findById(12640214L).get();
//            clipStatisticRepository.calculateWeekStatistics().forEach(System.out::println);
//            Thread.sleep(5 * 1000);
//            clipStatisticRepository.calculateWeekStatistics().forEach(System.out::println);
        };
    }

}
