package com.reza.learning;

import com.reza.learning.config.AppProperties;
import com.reza.learning.service.LuceneService;
import com.reza.learning.service.PersonFakerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@Slf4j
public class MainApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    @Autowired
    private AppProperties appProperties;
    @Autowired
    private LuceneService luceneService;
    @Autowired
    private PersonFakerService personFakerService;

    @Override
    public void run(String... args) {
        log.info("Starting Lucene Application");
        // Execute fake data generation if configured
        var fakeData = appProperties.fakeData();
        var lucene = appProperties.lucene();

        if (fakeData.execute()) {
            log.info("Executing PersonFakerService for fake data generation");
            personFakerService.fake();
        }
        if (lucene.index()) {
            log.info("Indexing files");
            luceneService.index();
        }

        log.info("Querying files");
        try {
            luceneService.query(args[0]);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
