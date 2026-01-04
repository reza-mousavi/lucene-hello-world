package com.reza.learning.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reza.learning.config.AppProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonFakerService {

    private final ObjectMapper objectMapper;
    private final AppProperties appProperties;

    public void fake() {
        var fakeData = appProperties.fakeData();
        Integer count = fakeData.count();
        String outputPath = fakeData.outputPath();
        log.info("Creating {} fake people in directory: {}", count, outputPath);
        fake(count, Path.of(outputPath));
    }

    /**
     * Generates fake Person records in parallel, serializes to JSON, and writes each to a file named by the person's uuid.
     *
     * @param count     Number of Person records to generate
     * @param outputDir Directory to write the JSON files
     */
    public void fake(int count, Path outputDir) {
        try {
            if (Files.exists(outputDir)) {
                log.info("Output directory {} exists. Cleaning up existing files.", outputDir);
                Files.list(outputDir).forEach(path -> {
                    try {
                        Files.delete(path);
                    } catch (IOException e) {
                        log.error("Could not delete file: {}", path, e);
                    }
                });
            } else {
                log.info("Creating output directory: {}", outputDir);
                Files.createDirectories(outputDir);
            }
        } catch (IOException e) {
            log.error("Could not create output directory: {}", outputDir, e);
            throw new RuntimeException(e);
        }
        AtomicInteger generated = new AtomicInteger();
        IntStream.range(0, count).parallel().forEach(i -> {
            var person = PersonFactory.create();
            String fileName = person.uuid() + ".json";
            Path filePath = outputDir.resolve(fileName);
            try {
                var json = objectMapper.writeValueAsString(person);
                Files.writeString(filePath, json);
                int soFar = generated.incrementAndGet();
                if (soFar % 100 == 0 || soFar == count) {
                    log.info("Generated {} records so far", soFar);
                }
            } catch (Exception e) {
                log.error("Failed to write person to file: {}", filePath, e);
            }
        });
    }
}
