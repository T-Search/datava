package de.tsearch.datava;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class DatavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(DatavaApplication.class, args);
    }

}
