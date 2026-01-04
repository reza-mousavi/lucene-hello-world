package com.reza.learning.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.config")
public record AppProperties(
        FakeData fakeData,
        Lucene lucene
) {

    public record FakeData(
            Boolean execute,
            Integer count,
            String outputPath
    ) {}

    public record Lucene(
            Boolean index,
            String indexPath,
            String docsPath,
            String field,
            Integer paging,
            Integer repeat,
            Boolean raw,
            Boolean create
    ) {}
}


